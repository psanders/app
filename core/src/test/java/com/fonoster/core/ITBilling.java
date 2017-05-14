package com.fonoster.core;

import com.fonoster.core.api.BillingAPI;
import com.fonoster.core.api.NumbersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.model.PhoneNumber;
import com.fonoster.model.Rate;
import com.fonoster.model.ServiceProvider;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

public class ITBilling {

    @Test(expected = ApiException.class)
    public void rateNoMatch() throws Exception {
        ServiceProvider sp = NumbersAPI.getInstance().getServiceProviders().get(0);
        Rate r = BillingAPI.getInstance().getRate(sp, "02121");
    }

    @Test
    public void testPrice() throws Exception {
        // Get the rate to terminate a call to X number from Y service provider.
        ServiceProvider sp = NumbersAPI.getInstance().getServiceProviders().get(0);
        Rate rate = BillingAPI.getInstance().getRate(sp, "17853178070");

        // Get the price for 60 seconds calling from our test number to psanders phone
        PhoneNumber pn = NumbersAPI.getInstance().getPhoneNumber("+17066041487");
        BigDecimal price = BillingAPI.getInstance().getPrice(pn, "17853178070", 60);

        assertTrue("The selling price and the price should match", price.doubleValue() == rate.getSelling().doubleValue());
    }
}
