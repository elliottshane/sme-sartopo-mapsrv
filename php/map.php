<?php 
/*  This is a basic example that outlines how to implement sartopo online
*   Mapping of a point. 
*
*   This is not working code, rather a quick explaintion of how I got mine working
*   This is based on the Slim Framework;
*/


$app->post('/point', function (Request $request, Response $response, array $args) {
    $body = $request->getParsedBody();
    $lat = $body['lat'];
    $lng = $body['lng'];
    $expires = $congig['expires']; // the unix date your subscription expires.
    $Key64 = $config['key64']; // Key Paramater (See Readme);
    $id = $config['id']; //your API ID (see Readme);
    $uri = "/api/v1/". $body['mapId'] ."/KT1J/Marker";

 
    $key = base64_decode($key64);
    
    $point = [
            "id"=> null,
            "properties"=>
            [
                "marker-symbol"=> "point",
                "marker-color"=> '#FF00FF',
                "title"=> "Test of API",
                "description"=> '',
                "folderId"=> null,
                "marker-rotation"=> null
            ],
            "geometry"=>
            [
                "type"=> "Point",
                "coordinates"=> [$lng, $lat]
            ]
            ];
    $payload = json_encode($point);
    $data = "POST" ." " . $uri ."\n". $expires . "\n" . $payload;
    $token = base64_encode(hash_hmac('sha256', $data ,$$key,true));
   
    $client = new Client([
        'base_uri' => 'https://sartopo.com',
    
        ]);
    
$response = $client->request('Post', $uri, [
     'form_params' => [
         'json'=>json_encode($point),
         'id'=>$id,
         'expires'=>$expires,
         'signature'=>$token
         ]
]);
   return $response;
    
});