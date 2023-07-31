package com.techvg.inventory.management.web.rest.errors;

public class GstNumberAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public GstNumberAlreadyUsedException() {
        super(ErrorConstants.GST_NUMBER_ALREADY_USED_TYPE, "Client gst number is already in use!", "", "gstNumberExists");
    }
}
