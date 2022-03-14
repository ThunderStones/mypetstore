$(() => {
    let shippingForm = $('.shippingAddress');
    shippingForm.slideUp(0);
    $('input[name="shippingAddressRequired"]').change(() => {
        if (($('input[name="shippingAddressRequired"]').is(':checked'))) {
            shippingForm.slideDown(300, 'swing');
            shippingForm.removeAttr("disabled")
        } else {
            shippingForm.slideUp(300, 'swing')
            shippingForm.attr('disabled')
        }

    })
})