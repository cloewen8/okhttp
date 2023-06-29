# Agra-GPS Changes
This branch includes changes specifically for Agra-GPS. They are not intended
for public use, although can be cherry-picked if desired.

## Changes
- Group names where changes from com.squareup to com.agragps.
- Created a CodecFactory for providing ExchangeCodecs.
- Added a codecFactory to the OkHttpClient.
- Used the codecFactory to create codecs in the RealConnection.
- Some internal objects used by Http1ExchangeCodec have been made public.
