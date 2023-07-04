package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository

        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        User user = userRepository.findById(userId).get();
        Subscription userSubscription = user.getSubscription();
        SubscriptionType subscriptionType = userSubscription.getSubscriptionType();

        // make three variables -
        int ctElite = 0;
        int ctPro = 0;
        int ctBasic = 0;

        for(WebSeries webSeries : webSeriesList){

            if(webSeries.getSubscriptionType() == SubscriptionType.ELITE ){

                if(user.getAge() >= webSeries.getAgeLimit()) ctElite++;

            }else if(webSeries.getSubscriptionType() == SubscriptionType.BASIC){
                if(user.getAge() >= webSeries.getAgeLimit()) ctPro++;
            }else {
                if (user.getAge() >= webSeries.getAgeLimit()) ctBasic++; // pro
            }
        }

        // filter on basis of age and subscription type and return answer as addition of all eligible based on subscription
        if(subscriptionType == SubscriptionType.ELITE){

            return ctElite + ctPro + ctBasic;
        }

        if(subscriptionType == SubscriptionType.BASIC){

            return ctBasic;
        }

        return ctBasic + ctPro;
    }
}
