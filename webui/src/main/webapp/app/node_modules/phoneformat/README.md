# PhoneFormat

This is a copy of [albeebe/phoneformat.js](https://github.com/albeebe/phoneformat.js) with the following changes:

  * This is a [Bower](http://bower.io) package
  * Website is not a part of the repository
  * Updating is as simple as typing `make` (assuming [`curl`](http://curl.haxx.se/) is installed)
  * Exposed getNumberType(country, phone) to return the type of the phone number (mobile, landline, toll free, etc.)

## Install

    bower install phoneformat
    
## Use

Reference `phoneformat.min.js` from your project.

Available functions:

  * countryForE164Number(phone)
  * formatNumberForMobileDialing(country, phone)
  * isValidNumber(phone, country)
  * formatE164(country, phone)
  * formatInternational(country, phone)
  * formatLocal(country, phone)
  * cleanPhone(phone)
  * countryCodeToName(country)

All of the above are subject to change in future versions.

# Todo

  * AMD
  * Better API
