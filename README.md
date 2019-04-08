# sme-sartopo-mapsrv
small node server for setting gps markers on sartopo-offline

This node server accepts http posts and then uses the sartopo api to set a marker on a given map.  For now, this functions with sartopo offline only.  I am workingon making it work with sartopo.com.

to use with sartopo offline:

1. clone the repository.
2. run with node . 

you should see:

Sartopo-offline map server running on *:4567

From your application, send a web post to http://your.server.name:4567/point

with json formatted like:

            { logId: '',
            team: '', //data used for marker label (if you pass a number only, it will add "Team-" to the number.)
            lat: 34.00989856554934,
            lng: -116.94253665575694,
            index: 3, //used by my app and for marker label if team is null
            mapId: 'XXXX' } //the sartopo mapID



# Sartopo.com

I am working on this working with sartopo.com.  From what I understand, I need to send the same info from above with:

1. id (Code paramaer described below)
2. expires (expiration date in ms).  This is the date of you subscription expiration.  
3. signature (a signature).  The signature is a hask of data as described below:


Signature:

The signature is a HMAC-SHA256 hash of the data below.  this signatre is compared on sartopo.com and if it is the same, adds the marker to the identified mapID.  

 data hashed in signature:

 1. method.  The method should be POST
 2. uri.  the uri is the url of the the map api without the hostname. /api/v1/map/{mapID}/Marker
 3. expires. explained above.
 4. payload.  the geo-json of the point.

 the signature is hashed by a key.  the key is btained with the code (id) desribed below. 
 
 in the format:
        
        method + " " + uri + "\n" + expires + "\n" + payload

Generating the signature. 

In Java:
Some java Matt provided that generates the signature:


        private static byte[] HmacSHA256(String data, byte[] key) throws Exception  {
            String algorithm="HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data.getBytes("UTF8"));
        }

        byte[] bytes = HmacSHA256(method + " " + uri + "\n" + expires + "\n" + (payload == null ? "" : payload), Base64.decode(key));
        return Base64.encode(bytes);

I have a java implementation of this from Matt's code in this repository under java/hmac2.  If you manually change:

                String method = "POST";
		String uri = "/api/v1/map/{mapId}/Marker";
		String payload = "";
		String expires = "";
		String id = "";  // Code Parameter described below.


In JsvaScript:
Here is what I have implemented to generatethe signature and use in this node app. The variables are set in the config.js file.

        expires: {Unix Timestamp}, //expiration of your subscription. 
        key64: "", //Key Parameter
        id: "", //Code Parameter


        let data = "POST" + " " + uri + "\n" + expires + "\n" + (payload == null ? "" : payload);
        let key = new Buffer(key64, 'base64');
        var hash = crypto.createHmac('SHA256', key).update(data).digest('base64');



 Update, I can now post to sartopo.com with signature.  I will add some API endpoints for my app and others soon. For now,the following is implemented:

at the console you can hit the following letters to test posting to sartopo.com:

* t = test posing with signature to sartopo.com
* m = post without signature, this I use to verify the geojson is correct and I post to sartopo offline. 
* x = jsut give you the hash for comparison. 


# Code.

Above I reference a Code and Key pair for use in generating the required signature for posting to sartop.com.  This info can be obtained as follows:

1. login into your sartopo account. 
2. click on this url [sartopo.com app activation](https://sartopo.com/app/activate/offline?redirect=localhost)
3. enable the developer console of your browser and go to the network tab. 
4. check the checkbox and click on syn account. 
5. you will notice a failed post with a code in it, copy the code.
6.replace your_code in the following url and paste into the browser. sartopo.com/api/v1/activate?code=your_code
7. you should get a page tha looks like the following:
'
  "code": "XXXXXXXXXXX",
  "account": {
    "id": "XXXXXX",
    "type": "Feature",
    "properties": {
      "subscriptionExpires": 1554760038,
      "subscriptionType": "pro-1",
      "subscriptionRenew": true,
      "subscriptionStatus": "active",
      "title": "......@sbsar",
      "class": "UserAccount",
      "updated": 1554760038,
      "email": "......@sbsar.org"
    }
  },
  "key": "xXXXXxXXXXXXXXXxxxXXXXxXxXXXXXXXXXXXX="
}'

the id is the value of code and ky is the value of key.

I have it running on my little sartopo offline box with https://www.npmjs.com/package/pm2
