package com.brewkery.app

import com.brewkery.app.data.model.CartItem
import com.brewkery.app.data.model.CartPricing
import com.brewkery.app.data.model.MenuItem
import com.brewkery.app.data.model.MilkOption
import com.brewkery.app.data.model.SizeOption
import com.brewkery.app.data.model.optionExtraPrice
import org.junit.Assert.assertEquals
import org.junit.Test

class CartPricingTest {
    @Test
    fun calculatesSubtotalDeliveryTaxAndTotal() {
        val item = CartItem(
            item = MenuItem(basePrice = 4.85),
            selectedSize = SizeOption(id = "large", extraPrice = 1.25),
            selectedMilk = MilkOption(id = "almond", extraPrice = 0.50),
            selectedSugar = "Light Wildflower Honey (+0.40)",
            quantity = 2
        )

        val summary = CartPricing.calculate(listOf(item), deliveryFee = 2.50, taxRatePercent = 8.0)

        assertEquals(14.00, summary.subtotal, 0.0001)
        assertEquals(2.50, summary.deliveryFee, 0.0001)
        assertEquals(1.12, summary.tax, 0.0001)
        assertEquals(17.62, summary.total, 0.0001)
    }

    @Test
    fun readsHoneySurchargeFromSugarOptionLabel() {
        assertEquals(0.40, optionExtraPrice("Light Wildflower Honey (+0.40)"), 0.0001)
        assertEquals(0.0, optionExtraPrice("Zero Added Sugar"), 0.0001)
    }
}
