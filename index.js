const http = require('http');
var got = require('got');
const express = require('express');
const bodyParser = require('body-parser');
const axios = require('axios');
var crypto = require('crypto');
var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }))
var stdin = process.openStdin();
const config = require('./config.js');
var clientUrl = "http://localhost:8080";
var sartopoOl = "http://localhost:8080";

//CORS Stuff change the url to the url of your app that is calling the api
app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", clientUrl);
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, PATCH, OPTIONS')
    res.header('Access-Control-Allow-Credentials', 'true');
    next();
});


app.post('/sartopo/resource/', (request, response) => {
    /*
    * accepts:
    *  {"resourceId":"","mapId":"","name":"","agency":""}
    *  do not include resourceId for new resouces, only for edit. 
    */
    let newResource = {
        "properties":
        {
            "title": request.body.name,
            "assignmentId": "",
            "type": "PERSON",
            "agency": request.body.agency
        },
        "id": request.body.resourceId
    }

    var formData = {
        json: JSON.stringify(newResource)
    }
    if (request.body.resourceId) {
        //console.log(request.body.resourceId)
        var mapUrl = sartopoOl + '/api/v1/map/' + request.body.mapId + '/Resource/' + request.body.resourceId
    }
    else {
        console.log(request.body)
        var mapUrl = sartopoOl + '/api/v1/map/' + request.body.mapId + '/Resource'
    }
    got(mapUrl, {
        form: true,
        
        body: formData,
    })
        .then(function (data) {
            response.send(data.body)
        }).catch(function (error) {
            console.log(error)
        })

})
app.delete('/sartopo/point', (request, response) => {
    console.log('delete....');
    if (request.body.markerId) {
        console.log(request.body.markerId)
        var mapUrl = sartopoOl + '/api/v1/map/' + request.body.mapId + '/Marker/' + request.body.markerId;
        got.delete(mapUrl)
            .then(function (data) {
                response.send(data.body)
            }).catch(function (error) {
                console.log(error);
            });
    }
    else {
        console.log('no ID')
    }
});

app.post('/sartopo/point', (request, response) => {

    //console.log(request.body)
    //the colors are used to change the team colors
    var color = [
        '#0000FF',//BLUE	
        '#008000',//GREEN
        '#800080',//PURPLE	
        '#FF0000',//RED	
        '#000000',//BLACK
        '#800000',//MAROON	
        '#FFFF00',//YELLOW	
        '#808000',//OLIVE	
        '#00FF00',//LIME	
        '#00FFFF',//AQUA	
        '#008080',//TEAL	
        '#808080',//GRAY	
        '#000080',//NAVY	
        '#FF00FF',//FUCHSIA	
        '#C0C0C0',//SILVER
    ];

    if (Number(request.body.team) && Number(request.body.team) < 15) {
        var markerColor = color[Number(request.body.team)];
    }
    else {
        var markerColor = color[4]
    }
    if (request.body.TimeStamp == undefined) {
        // console.log('here')
        var TimeStamp = new Date().toLocaleString().split(' ')[1]
    }
    else {
        var TimeStamp = request.body.TimeStamp.split(' ')[1]
    }


    // console.log(request.body)
    if (request.body.team === null) {
        request.body.team = request.body.eventID + "@" + TimeStamp;
    }
    else {
        request.body.team = request.body.team + "@" + TimeStamp.substring(0, 5);
    }

    let newPoint = {
        "id": request.body.markerId ? request.body.markerId : null,
        "properties":
        {
            "marker-symbol": "point",
            "marker-color": markerColor,
            "title": request.body.team,
            "description": '',
            "folderId": null,
            "marker-rotation": null
        },
        "geometry":
        {
            "type": "Point",
            "coordinates": [request.body.lng, request.body.lat]
        }
    };
    var formData = {
        json: JSON.stringify(newPoint)
    };
    //console.log(formData);
    if (request.body.markerId) {
        console.log(request.body.markerId)
        var mapUrl = sartopoOl + '/api/v1/map/' + request.body.mapId + '/Marker/' + request.body.markerId
    }
    else {
        console.log(request.body)
        var mapUrl = sartopoOl + '/api/v1/map/' + request.body.mapId + '/Marker'
    }
    got(mapUrl, {
        form: true,
        body: formData,
    })
        .then(function (data) {
            response.send(data.body)
        }).catch(function (error) {
            console.log(error)
        });                                                                                                                                                                                                                                                                                                                                                              
})

stdin.addListener("data", function (d) {

    const { mapId, host, expires, key64, id, loc } = config;
    var url=  host + "/api/v1/map/" + mapId + "/Marker";
    var uri = "/api/v1/map/" + mapId + "/Marker";

   
    if (d.toString().trim() === 'm') {
        console.log("--NO Hash---");
        
        let newPoint = {
            "id": null,
            "properties":
            {
                "marker-symbol": "point",
                "marker-color": '#FF00FF',
                "title": "Test of API",
                "description": '',
                "folderId": null,
                "marker-rotation": null
            },
            "geometry":
            {
                "type": "Point",
                "coordinates": loc
            }
        }
        console.log("sending....")
        var formData = {
            json: JSON.stringify(newPoint),
        };
        console.log(formData);
        got(sartopoOl + "/api/v1/map/" + mapId + "/Marker", {
            form: true,
            body: formData,
        })
            .then(function (data) {
                console.log(data.body);
            }).catch(function (error) {
                console.log(error);
            });
    }
    //t test signature hash with post
    if (d.toString().trim() === 't') {
        console.log("--POST With HASH---")
        let point = {
            "id": null,
            "properties":
            {
                "marker-symbol": "point",
                "marker-color": '#FF00FF',
                "title": "Test of API",
                "description": '',
                "folderId": null,
                "marker-rotation": null
            },
            "geometry":
            {
                "type": "Point",
                "coordinates": loc
            }
        };

        let payload = JSON.stringify(point)
        //let payload = "test";
        let data = "POST" + " " + uri + "\n" + expires + "\n" + (payload == null ? "" : payload);
        let key = new Buffer(key64, 'base64');
        var hash = crypto.createHmac('SHA256', key).update(data).digest('base64');
        //post the stuff
        let formData = {
            json: JSON.stringify(point),
            id: id,
            expires: expires,
            signature: hash
        };
        console.log(formData);
        console.log(url)
        got(url, {
            form: true,
            body: formData,
            })
            .then(function (data) {
                console.log(data.body)
            }).catch(function (error) {
                console.log(error)
            });
    }


    if (d.toString().trim() === 'x') {
        console.log("--HASH---")

        let point = {
            "id": null,
            "properties":
            {
                "marker-symbol": "point",
                "marker-color": '#FF00FF',
                "title": "Test of API",
                "description": '',
                "folderId": null,
                "marker-rotation": null
            },
            "geometry":
            {
                "type": "Point",
                "coordinates": loc
            }
        };

        let payload = JSON.stringify(point)

        let data = "POST" + " " + uri + "\n" + expires + "\n" + (payload == null ? "" : payload);
        //let data = 'test';
        let key = new Buffer(key64, 'base64');
        var hash = crypto.createHmac('SHA256', key).update(data).digest('base64');
        var hashHex = crypto.createHmac('SHA256', key).update(data).digest('hex');
        //post the stuff
        let formData = {
            json: JSON.stringify(point),
            id: id,
            expires: expires,
            signature: hash
        };
        console.log("post Data", formData);
        console.log('-------------');
        console.log("hash:", hash);
        console.log('-------------');
        console.log("hashHex",hashHex);
        console.log('-------------');
        console.log("data",data);
        console.log("--------------");
        console.log(key);
        console.log("--------------")

    }
   
});

app.get('/sartopo/version', (request, response) => {
    response.send("version 1.0")
})


var server = http.createServer(app);

var port = process.env.PORT || 4567;
console.log(port)
server.listen(port, () => {
    console.log('Sartopo-offline map server running on *:' + port);
});