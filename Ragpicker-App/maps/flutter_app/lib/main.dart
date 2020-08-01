import 'package:flutter_polyline_points/flutter_polyline_points.dart';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'components/map_pin_pill.dart';
import 'dart:async';

import 'models/pin_pill_info.dart';

import 'package:firebase_database/firebase_database.dart';
import 'screens/marks.dart';
import 'screens/captureImg.dart';

void main() => runApp(
    MaterialApp(
        debugShowCheckedModeBanner: false,
        home: MapPage()
    )
);

const double CAMERA_ZOOM = 13;
const double CAMERA_TILT = 0;
const double CAMERA_BEARING = 30;
const LatLng SOURCE_LOCATION = LatLng(42.7477863, -71.1699932);
const LatLng DEST_LOCATION = LatLng(42.6871386, -71.2143403);

class MapPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => MapPageState();
}

class MapPageState extends State<MapPage> {

  Completer<GoogleMapController> _controller = Completer();
  Set<Marker> _markers = {};
  List<Marker> marksList = [];
  Set<Polyline> _polylines = {};
  List<LatLng> polylineCoordinates = [];
  PolylinePoints polylinePoints = PolylinePoints();
  String googleAPIKey = "<YOUR_API_KEY>";
  BitmapDescriptor sourceIcon;
  BitmapDescriptor destinationIcon;
  double pinPillPosition = -100;
  PinInformation currentlySelectedPin = PinInformation(pinPath: '', avatarPath: '', location: LatLng(0, 0), locationName: '', labelColor: Colors.grey);
  PinInformation sourcePinInfo;
  PinInformation destinationPinInfo;

  @override
  void initState() {
    super.initState();
    setSourceAndDestinationIcons();
  }

  void setMapPins() {
    // source pin

    DatabaseReference marksRef = FirebaseDatabase.instance.reference().child("Detected");

    marksRef.once().then((DataSnapshot snap){
      var KEYS = snap.value.keys;
      var DATA = snap.value;

      marksList.clear();

      for(var individualKey in KEYS) {
        Marks marks = new Marks(
          DATA[individualKey]['latitude'],
          DATA[individualKey]['longitude'],
          DATA[individualKey]['Area'],
          DATA[individualKey]['Pincode'],
        );

        marksList.add(Marker(
          markerId: MarkerId('marker'),
          onTap: () {
//            Navigator.push(
//                context, MaterialPageRoute(builder: (context) =>
//                Capture()));
            setState(() {
              currentlySelectedPin = sourcePinInfo;
              pinPillPosition = 0;
            });
          },
          icon: sourceIcon,
          position: LatLng(marks.latitude, marks.longitude),
        ),);
      }
//    _markers.add(Marker(
//
//      // This marker id can be anything that uniquely identifies each marker.
//        markerId: MarkerId('sourcePin'),
//        position: SOURCE_LOCATION,
//        onTap: () {
//          setState(() {
//            currentlySelectedPin = sourcePinInfo;
//            pinPillPosition = 0;
//          });
//        },
//        icon: sourceIcon
//    ));

    sourcePinInfo = PinInformation(
        locationName: "Start Location",
        location: SOURCE_LOCATION,
        pinPath: "assets/driving_pin.png",
        avatarPath: "assets/friend1.jpg",
        labelColor: Colors.blueAccent
    );

    // destination pin
//    _markers.add(Marker(
//      // This marker id can be anything that uniquely identifies each marker.
//        markerId: MarkerId('destPin'),
//        position: DEST_LOCATION,
//        onTap: () {
//          setState(() {
//            currentlySelectedPin = destinationPinInfo;
//            pinPillPosition = 0;
//          });
//        },
//        icon: destinationIcon
//    ));
//
//    destinationPinInfo = PinInformation(
//        locationName: "End Location",
//        location: DEST_LOCATION,
//        pinPath: "assets/destination_map_marker.png",
//        avatarPath: "assets/friend2.jpg",
//        labelColor: Colors.purple
//    );
  });
        }

  void setSourceAndDestinationIcons() async {
    sourceIcon = await BitmapDescriptor.fromAssetImage(
        ImageConfiguration(devicePixelRatio: 2.5), 'assets/driving_pin.png');

//    destinationIcon = await BitmapDescriptor.fromAssetImage(
//        ImageConfiguration(devicePixelRatio: 2.5), 'assets/destination_map_marker.png');
  }

  void onMapCreated(GoogleMapController controller) {
    controller.setMapStyle(Utils.mapStyles);
    _controller.complete(controller);

    setMapPins();
//    setPolylines();
  }

  @override
  Widget build(BuildContext context) {

    CameraPosition initialLocation = CameraPosition(
        zoom: CAMERA_ZOOM,
        bearing: CAMERA_BEARING,
        tilt: CAMERA_TILT,
        target: SOURCE_LOCATION
    );

    return Scaffold(
        body: Stack(
            children: <Widget>[
              GoogleMap(
                myLocationEnabled: true,
                compassEnabled: true,
                tiltGesturesEnabled: false,
                markers: Set.from(marksList),
                polylines: _polylines,
                mapType: MapType.normal,
                initialCameraPosition: initialLocation,
                onMapCreated: onMapCreated,
                onTap: (LatLng location) {
                  setState(() {
                    pinPillPosition = -100;
                  });
                },
              ),
              MapPinPillComponent(
                  pinPillPosition: pinPillPosition,
                  currentlySelectedPin: currentlySelectedPin
              )
            ])
    );
  }

//  setPolylines() async
//  {
//    List<PointLatLng> result = await polylinePoints.getRouteBetweenCoordinates(googleAPIKey,
//        SOURCE_LOCATION.latitude, SOURCE_LOCATION.longitude,
//        DEST_LOCATION.latitude, DEST_LOCATION.longitude);
//
//    if(result.isNotEmpty){
//      result.forEach((PointLatLng point){
//        polylineCoordinates.add(LatLng(point.latitude, point.longitude));
//      });
//
//      setState(() {
//        Polyline polyline = Polyline(
//            polylineId: PolylineId("poly"),
//            color: Color.fromARGB(255, 40, 122, 198),
//            points: polylineCoordinates
//        );
//        _polylines.add(polyline);
//      });
//    }
  }

class Utils {
  static String mapStyles = '''[
  {
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#f5f5f5"
      }
    ]
  },
  {
    "elementType": "labels.icon",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#616161"
      }
    ]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [
      {
        "color": "#f5f5f5"
      }
    ]
  },
  {
    "featureType": "administrative.land_parcel",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#bdbdbd"
      }
    ]
  },
  {
    "featureType": "poi",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#eeeeee"
      }
    ]
  },
  {
    "featureType": "poi",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#e5e5e5"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#9e9e9e"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#ffffff"
      }
    ]
  },
  {
    "featureType": "road.arterial",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#dadada"
      }
    ]
  },
  {
    "featureType": "road.highway",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#616161"
      }
    ]
  },
  {
    "featureType": "road.local",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#9e9e9e"
      }
    ]
  },
  {
    "featureType": "transit.line",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#e5e5e5"
      }
    ]
  },
  {
    "featureType": "transit.station",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#eeeeee"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#c9c9c9"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#9e9e9e"
      }
    ]
  }
]''';
}