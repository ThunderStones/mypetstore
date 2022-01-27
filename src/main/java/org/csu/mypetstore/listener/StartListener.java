package org.csu.mypetstore.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class StartListener implements ApplicationListener<AvailabilityChangeEvent> {

    @Autowired
    private ServletContext servletContext;

    @Override
    public void onApplicationEvent(AvailabilityChangeEvent event) {
        if (ReadinessState.ACCEPTING_TRAFFIC == event.getState()) {
            List<String> LANGUAGE_LIST = Collections.unmodifiableList(Arrays.asList("English", "Japanese"));
            List<String> CATEGORY_LIST = Collections.unmodifiableList(Arrays.asList("FISH", "DOGS", "REPTILES", "CATS", "BIRDS"));
            List<String> CARD_TYPE_LIST = Collections.unmodifiableList(Arrays.asList("Visa", "MasterCard", "American Express"));
            servletContext.setAttribute("languageList", LANGUAGE_LIST);
            servletContext.setAttribute("categoryList", CATEGORY_LIST);
            servletContext.setAttribute("cardTypeList", CARD_TYPE_LIST);
            System.out.println("start listener");
        }

    }
}
