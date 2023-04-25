package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuctionRequest;
import io.turntabl.ttbay.dto.AuctionResponseDTO;
import io.turntabl.ttbay.dto.EditAuctionRequestDTO;
import io.turntabl.ttbay.exceptions.*;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Bid;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.AuctionRepository;
import io.turntabl.ttbay.repository.BidRepository;
import io.turntabl.ttbay.repository.ItemRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.*;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static io.turntabl.ttbay.enums.AuctionStatus.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


import static io.turntabl.ttbay.enums.AuctionStatus.SCHEDULED;
import static io.turntabl.ttbay.enums.AuctionStatus.LIVE;

@AllArgsConstructor
@Service

public class AuctionServiceImpl implements AuctionService{
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final TokenAttributesExtractor tokenAttributesExtractor;
    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;
    private final AuctionMapperService auctionMapperService;
    private final EmailTriggerService emailTriggerService;

    @Override
    public List<AuctionResponseDTO> returnAllAuctionByUser(Authentication authentication) throws ResourceNotFoundException{
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        User targetUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Auction> allAuctions = auctionRepository.findByAuctioner(targetUser);
        return allAuctions.stream().map(auctionMapperService::returnAuctionResponse).toList();
    }

    @Override
    public AuctionResponseDTO returnOneAuctionOfUser(Long auctionId) throws ResourceNotFoundException{
        Auction auction = returnOneAuction(auctionId);
        return auctionMapperService.returnAuctionResponse(auction);
    }

    @Override
    public String createAuction(AuctionRequest auctionRequest, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException{
        Item item = itemService.returnOneItem(authentication, auctionRequest.itemId());
        try{
            if (item.getOnAuction()){
                throw new ItemAlreadyOnAuctionException("This is item is already on auction");
            }
            Auction newAuction = Auction.builder().reservedPrice(auctionRequest.price()).auctioner(item.getUser()).item(item).startDate(auctionRequest.startDate()).endDate(auctionRequest.endDate()).status(auctionRequest.status()).build();
            item.setOnAuction(true);
            itemRepository.save(item);
            auctionRepository.save(newAuction);
            return "Auction created successfully";
        }catch (Exception exception) {
            throw new ModelCreateException("Error creating models");
        }
    }
    public String cancelAuctionWithBidChecking(Long auctionId, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException {
        String tokenEmail = tokenAttributesExtractor.extractEmailFromToken(authentication);
        //get auction
        Auction targetAuction = returnOneAuction(auctionId);
        if (!targetAuction.getAuctioner().getEmail().equalsIgnoreCase(tokenEmail)){
            throw new MismatchedEmailException("You don't have access to this action");
        }
        //check for available bids
        List<Bid> availableBids = bidRepository.findByAuction(targetAuction);
        if (!availableBids.isEmpty()) {
            return "Auction has bid(s), cannot be cancelled";
        }
        //set item to draft
        targetAuction.getItem().setOnAuction(Boolean.FALSE);
        //set auction to cancelled
        targetAuction.setStatus(CANCELLED);
        auctionRepository.save(targetAuction);
        return "Auction cancelled successfully";
    }

    @Override
    public Auction returnOneAuction(Long auctionId) throws ResourceNotFoundException{
        Optional<Auction> auction = auctionRepository.findById(auctionId);
        if (auction.isEmpty()) {
            throw new ResourceNotFoundException("Auction not found");
        }
        return auction.get();
    }

    public String cancelAuction(Long auctionId, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException{
        String tokenEmail = tokenAttributesExtractor.extractEmailFromToken(authentication);
        //get auction
        Auction targetAuction  =  returnOneAuction(auctionId);
        if (!targetAuction.getAuctioner().getEmail().equalsIgnoreCase(tokenEmail)){
            throw new MismatchedEmailException("You don't have access to this action");
        }
        //set item to draft
        targetAuction.getItem().setOnAuction(Boolean.FALSE);
        //set auction to cancelled
        targetAuction.setStatus(CANCELLED);
        auctionRepository.save(targetAuction);
        return  "Auction cancelled successfully";
    }

    public List<AuctionResponseDTO> returnAllAuctions(){
        List<Auction> allAuctions = auctionRepository.findAll();
        return allAuctions.stream().map(auctionMapperService::returnAuctionResponse).toList();
    }

    public void updateCurrentHighestBidOfAuction(Auction auction, Double highestBid){
        auction.setCurrentHighestBid(highestBid);
    }

    public List<Auction> returnAllAuction() throws ResourceNotFoundException{
        List<Auction> allAuctions = auctionRepository.findAll();
        if (allAuctions.isEmpty()) throw new ResourceNotFoundException("Empty Auctions");
        return allAuctions;
    }


    private List<Auction> getDraftAuctionsWithPastStartDates() throws ResourceNotFoundException{
        Date timeNow = new Date(System.currentTimeMillis());
        List<Auction> allAuctions = returnAllAuction();
        return  allAuctions.parallelStream()
                .filter(auction -> auction.getStatus() == SCHEDULED && timeNow.after(auction.getStartDate())).collect(Collectors.toList());
    }

    private void setAuctionStatusToLive(Auction auction){
        auction.setStatus(LIVE);
    }

    @Override
    public AuctionResponseDTO updateAuctionWithNoBid(EditAuctionRequestDTO editAuctionRequestDTO, Authentication authentication) throws MismatchedEmailException, ForbiddenActionException, ResourceNotFoundException{
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        Auction auctionToEdit = validateAuctionUpdateRequest(editAuctionRequestDTO, email);
        if (editAuctionRequestDTO.reservedPrice() != null) auctionToEdit.setReservedPrice(editAuctionRequestDTO.reservedPrice());
        if (editAuctionRequestDTO.endDate() != null) auctionToEdit.setEndDate(editAuctionRequestDTO.endDate());
        return auctionMapperService.returnAuctionResponse(auctionRepository.save(auctionToEdit));
    }

    private Map<User, Double> returnAllBiddersAndRespectiveBidAmountsOnAuction(Auction auction) throws ResourceNotFoundException{
        Map<User, Double> usersAndRespectiveBids = new HashMap<>();
        List<Bid> bidsOnAuction = bidRepository.findByAuction(auction);
        if (bidsOnAuction.isEmpty()){
            throw new ResourceNotFoundException("There are no bids on this auction");
        }
        bidsOnAuction.parallelStream().forEach(bid -> usersAndRespectiveBids.put(bid.getBidder(), bid.getBidAmount()));
        return usersAndRespectiveBids;
    }

    private Map<User,Double> getHighestBidder(Map<User, Double> usersAndRespectiveBids) throws ResourceNotFoundException{
        OptionalDouble max = usersAndRespectiveBids.values().parallelStream().mapToDouble(Double::doubleValue).max();
        if (max.isPresent()){
            Optional<User> key = usersAndRespectiveBids.entrySet().stream().filter(entry -> entry.getValue() == max.getAsDouble()).map(Map.Entry::getKey).findFirst();
            if (key.isPresent()){
                return Map.of(key.get(), max.getAsDouble());
            } else throw new ResourceNotFoundException("max bid not found");
        }
        return null;
    }

    private void setAuctionStatusToEnd(Auction auction){
        auction.setStatus(END);
    }

    private List<Auction> getLiveAuctionsWithPastEndDates() throws ResourceNotFoundException{
        Date timeNow = new Date(System.currentTimeMillis());
        List<Auction> allAuctions = returnAllAuction();
        return allAuctions.parallelStream().filter(auction -> auction.getStatus() == LIVE && timeNow.after(auction.getEndDate())).collect(Collectors.toList());
    }

    private Auction validateAuctionUpdateRequest(EditAuctionRequestDTO editAuctionRequestDTO, String email) throws ResourceNotFoundException, MismatchedEmailException, ForbiddenActionException{
        Optional<Auction> auctionResult = auctionRepository.findById(editAuctionRequestDTO.auctionId());
        if (auctionResult.isPresent() && !Objects.equals(auctionResult.get().getAuctioner().getEmail(), email))
            throw new MismatchedEmailException("You don't have access to this resource");
        if (auctionResult.isEmpty()) throw new ResourceNotFoundException("Item not found");
        if (!auctionResult.get().getBids().isEmpty())
            throw new ForbiddenActionException("Cannot perform this action because item has bids");
        return auctionResult.get();
    }

    public String auctionWinnerMarkAuctionedItemAsReceived(Long auctionId, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException{
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        Auction auction =  auctionRepository.findById(auctionId).orElse(null);
        if(auction == null){
            throw  new ResourceNotFoundException("auction not found");
        }
        if(!Objects.equals(auction.getWinner().getEmail(), email)){
            throw new MismatchedEmailException("you cannot perform this operation");
        }
        Item item = auction.getItem();
        item.setHighestBidderReceivedItem(true);
        itemRepository.save(item);
        return "item marked as received";
    }

    public String auctioneerMarkAuctionedItemAsDelivered(Long auctionId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException{
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        Auction auction =  auctionRepository.findById(auctionId).orElse(null);
        if(auction == null){
            throw  new ResourceNotFoundException("auction not found");
        }
        if(!Objects.equals(auction.getAuctioner().getEmail(),email)){
            throw new MismatchedEmailException("you do not own this auction");
        }
        Item item = auction.getItem();
        item.setAuctioneerHandItemToHighestBidder(true);
        itemRepository.save(item);
        return "item marked as delivered";
    }

    private void setHighestBidderAsWinner(Auction auction, User user){
        auction.setWinner(user);
    }

    private List<Auction> getAllEndedAuctionsWithoutWinners() throws ResourceNotFoundException{
        List<Auction> allAuctions = returnAllAuction();
        return allAuctions.parallelStream().filter(auction -> auction.getWinner() == null && auction.getStatus() == END).collect(Collectors.toList());
    }

    //run every five(5) minutes
    @Scheduled(cron = "0 */5  * * * *")
    @Async
    public CompletableFuture<Void> updateDraftAuctionToLiveAndPersistInDatabase() throws ResourceNotFoundException {
        List<Auction> draftAuctionsWithPastStartDates = getDraftAuctionsWithPastStartDates();
        draftAuctionsWithPastStartDates.parallelStream().forEach(auction -> {
            setAuctionStatusToLive(auction);
            auctionRepository.save(auction);
        });
        return CompletableFuture.completedFuture(null);
    }

    //run every four(4) minutes
    @Scheduled(cron = "0 */4 * * * *")
    @Async
    public CompletableFuture<Void> updateLiveAuctionToEndAndPersistInDatabase() throws ResourceNotFoundException {
        List<Auction> liveAuctionsWithPastEndDates = getLiveAuctionsWithPastEndDates();

        liveAuctionsWithPastEndDates.parallelStream().forEach(auction -> {
            setAuctionStatusToEnd(auction);
            auctionRepository.save(auction);
        });
        return CompletableFuture.completedFuture(null);
    }

    //run every six(6) minutes
    @Scheduled(cron = "0 */6 * * * *")
    @Transactional
    @Async
    public CompletableFuture<Void> updateAuctionWithWinnerAndBidAmount() throws ResourceNotFoundException{
        List<Auction> endedAuctionsWithoutWinners = getAllEndedAuctionsWithoutWinners();
        endedAuctionsWithoutWinners.parallelStream().forEach(auction -> {
            try{
                Map<User, Double> usersAndRespectiveBids = returnAllBiddersAndRespectiveBidAmountsOnAuction(auction);
                Map<User, Double> WinnerAndBidAmount = getHighestBidder(usersAndRespectiveBids);
                if(WinnerAndBidAmount != null){
                    User winner = WinnerAndBidAmount.keySet().stream().findFirst().orElseThrow();
                    Double highestBidAmount = WinnerAndBidAmount.values().stream().findFirst().orElseThrow();
                    setHighestBidderAsWinner(auction, winner);
                    auction.setWinningPrice(highestBidAmount);
                    auctionRepository.save(auction);
                    emailTriggerService.sendBidWinnerEmail(auction);
                    emailTriggerService.sendAuctioneerAfterHighestWinEmail(auction);
                }else throw new ResourceNotFoundException("WinnerAndBidAmount is null");

            } catch (ResourceNotFoundException | MessagingException e) {
                throw new RuntimeException(e);
            }
        });
        return CompletableFuture.completedFuture(null);
    }
}



