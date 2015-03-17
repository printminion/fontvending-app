#sample demo client which uses Font Vending App

# Source #
  * Svn
```
svn checkout http://fontvending-app.googlecode.com/svn/sandbox/mkFontVendingClient .
```

  * Source of example client [mkFontVendingClient.zip](http://code.google.com/p/fontvending-app/downloads/detail?name=mkFontVendingClient.zip&can=2&q=#makechanges)

  * Compiled version: [mkFontVendingClient\_v1.0.apk](http://code.google.com/p/fontvending-app/downloads/detail?name=mkFontVendingClient_v1.0.apk&can=2&q=#makechanges)


# Call Font Vending #
## Download fonts into default folder ##
```
Intent intent = new Intent(android.content.Intent.ACTION_GET_CONTENT);
intent.setPackage("com.kupriyanov.fontvending");
intent.setType("font/*");
startActivityForResult(intent, REQUEST_NEW_FONTS);
```
## Download fonts into custom folder ##
```
Intent intent = new Intent(android.content.Intent.ACTION_GET_CONTENT);
intent.setPackage("com.kupriyanov.fontvending");
intent.setType("font/*");

intent.putExtra("com.kupriyanov.fontvending.extra.CLIENT_PACKAGE", "com.your.app.package.name");
                        intent.putExtra("com.kupriyanov.fontvending.extra.TARGET_PATH", "/sdcard/some_custom_location");

startActivityForResult(intent, REQUEST_NEW_FONTS);
```
## Get download result ##

```

    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

                if (requestCode == REQUEST_NEW_FONTS) {

                        switch (resultCode) {
                        case RESULT_VENDING_FONT_INSTALLED:
                                Toast.makeText(getApplicationContext(), "some fonts are installed", Toast.LENGTH_SHORT).show();
                                populateSpinner();

                                break;
                        case RESULT_VENDING_FONT_UNINSTALLED:
                                Toast.makeText(getApplicationContext(), "some fonts are removed", Toast.LENGTH_SHORT).show();

                                populateSpinner();

                                break;

                        default:
                                Toast.makeText(getApplicationContext(), "no font installed or removed", Toast.LENGTH_SHORT).show();

                                break;
                        }
                }
                super.onActivityResult(requestCode, resultCode, data);
        }
```

# Market Search #
If you want that your application will be listed in Font Vending market search just put "Uses:com.kupriyanov.fontvending" in your application description and the market description.

See: [AndroidManifest.xml](http://code.google.com/p/fontvending-app/source/browse/sandbox/mkFontVendingClient/AndroidManifest.xml)
and
[strings.xml](http://code.google.com/p/fontvending-app/source/browse/sandbox/mkFontVendingClient/res/values/strings.xml)
```
<string name="application_description">Uses:com.kupriyanov.fontvending</string>
```