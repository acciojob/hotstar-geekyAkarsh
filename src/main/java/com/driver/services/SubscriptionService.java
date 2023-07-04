package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import jdk.internal.loader.AbstractClassLoaderValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Integer amount = 0;

        if(subscriptionEntryDto.getSubscriptionType() == SubscriptionType.ELITE){
            amount = 1000 + 350*subscriptionEntryDto.getNoOfScreensRequired();;
        }else if(subscriptionEntryDto.getSubscriptionType() == SubscriptionType.PRO){
            amount = 800 + 250*subscriptionEntryDto.getNoOfScreensRequired();;
        }else{
            amount = 500 + 200*subscriptionEntryDto.getNoOfScreensRequired();
        }

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setStartSubscriptionDate(new Date());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setTotalAmountPaid(amount);
        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);

        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        SubscriptionType userSubscriptionType = user.getSubscription().getSubscriptionType();

        if(userSubscriptionType == SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }

        if(userSubscriptionType == SubscriptionType.PRO){

            // upgrade to elite and return the price difference

            Subscription subscription = new Subscription();
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(1000 + (350*user.getSubscription().getNoOfScreensSubscribed()));
            subscription.setNoOfScreensSubscribed(user.getSubscription().getNoOfScreensSubscribed());
            subscription.setStartSubscriptionDate(user.getSubscription().getStartSubscriptionDate());
            int prevMoney = user.getSubscription().getTotalAmountPaid();

            subscription.setUser(user);
            user.setSubscription(subscription);
            userRepository.save(user);
            return subscription.getTotalAmountPaid() - prevMoney;
        }

        // else it is basic
        // In this case upgrade it to Pro and return the price difference

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionType(SubscriptionType.PRO);
        subscription.setTotalAmountPaid(800 * (250*user.getSubscription().getNoOfScreensSubscribed()));
        subscription.setNoOfScreensSubscribed(user.getSubscription().getNoOfScreensSubscribed());
        subscription.setStartSubscriptionDate(user.getSubscription().getStartSubscriptionDate());
        int prevMoney = user.getSubscription().getTotalAmountPaid();

        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);

        return subscription.getTotalAmountPaid() - prevMoney;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        Integer revenue = 0;

        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        for(Subscription subscription : subscriptionList){

            revenue += subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
