import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:firebase_database/firebase_database.dart';
import 'Marks.dart';
import 'captureImg.dart';
import 'package:maps/services/auth.dart';

import 'package:firebase_messaging/firebase_messaging.dart';

FirebaseMessaging _firebaseMessaging = FirebaseMessaging();

//void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  MyApp(this.uid) : super();

  final String uid;
  
  @override
  _MyAppState createState() => _MyAppState();
}

const double CAMERA_ZOOM = 13;
const double CAMERA_TILT = 0;
const double CAMERA_BEARING = 30;

FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin;

class _MyAppState extends State<MyApp> {

  Completer<GoogleMapController> _controller = Completer();

  static const LatLng _center = const LatLng(22.5726, 88.3639);
  final Set<Marker> _markers = {};
  LatLng _lastMapPosition = _center;
  MapType _currentMapType = MapType.normal;
  Position position;
  Widget _child;
  List<Placemark> placemark;
  String _address,area,pincode;
  double _lat,_lng;
  Map<MarkerId, Marker> markers = <MarkerId, Marker> {};
  List<Marker> marksList = [];
  StreamSubscription<Event> _onTodoAddedSubscription;


  @override
  void initState(){
    //_child = SpinKitDualRing("Getting Location");
//    getCurrentLocation();
//    populateClients();
    super.initState();

    flutterLocalNotificationsPlugin = new FlutterLocalNotificationsPlugin();
    var android = new AndroidInitializationSettings('mipmap/ic_launcher');
    var ios = new IOSInitializationSettings();
    var initSettings = new InitializationSettings(android,ios);
    flutterLocalNotificationsPlugin.initialize(initSettings,onSelectNotification: selectNotification);


    DatabaseReference marksRef = FirebaseDatabase.instance.reference().child("users").child(widget.uid).child("Tasks");

    marksRef.once().then((DataSnapshot snap){
      var KEYS = snap.value.keys;
      var DATA = snap.value;

      marksList.clear();

      for(var individualKey in KEYS) {
        Marks marks = new Marks(
          DATA[individualKey]['latitude'],
          DATA[individualKey]['longitude'],
        );
        marksList.add(Marker(
          markerId: MarkerId('marker'),
          onTap: (){

          },
          position: LatLng(marks.latitude,marks.longitude),
        ),);
      }

      setState(() {
        print('Length - $marksList.length');
      });
    });

    FirebaseDatabase _database = FirebaseDatabase.instance;
    DatabaseReference ref = _database.reference().child("users").child(widget.uid).child("Tasks");

    _onTodoAddedSubscription = ref.onChildAdded.listen(_onEntryadded);

  }
  _onEntryadded(Event event){

    showNotifications();
  }

  Future selectNotification(String payload){
    showDialog(context: context,builder: (_) => new AlertDialog(
      title: new Text('Notification'),
      content: new Text('$payload'),
    ));

  }

  showNotifications() async{
    var android = new AndroidNotificationDetails('channel id', 'channel NAME', 'channel DESC');
    var ios = new IOSNotificationDetails();
    var platform = new NotificationDetails(android, ios);
    await flutterLocalNotificationsPlugin.show(0,'Notification','New Task Assigned!!',platform,payload: 'New Task Assigned');
  }

  void setupNotification() async{
    _firebaseMessaging.getToken().then((token){
      print(token);
    });

    _firebaseMessaging.configure(
      onMessage: (Map<String,dynamic> message) async{
        print("onMessage called");
      },
      onResume: (Map<String,dynamic> message) async{
        print("onResume called");
      },
      onLaunch: (Map<String,dynamic> message) async{
        print("onLaunch called");
      },
    );


  }

//  void getCurrentLocation() async {
//    Position res = await Geolocator().getCurrentPosition();
//    setState(() {
//      position = res;
//      _lat = position.latitude;
//      _lng = position.longitude;
//    });
//    await getAddress(_lat,_lng);
//  }
//
//  void getAddress(double latitude, double longitude) async{
//    placemark = await Geolocator().placemarkFromCoordinates(latitude, longitude);
//    _address = placemark[0].name.toString() + "," + placemark[0].locality.toString() + ", Postal code - " + placemark[0].postalCode.toString();
//    setState(() {
//      _child = mapWidget();
//    });
//  }

//  populateClients(){
//    Firestore.instance.collection('Waste details').getDocuments().then((docs){
//      if(docs.documents.isNotEmpty){
//        for(int i=0;i<docs.documents.length;++i){
//          initMarker(docs.documents[i].data,docs.documents[i].documentID);
//        }
//      }
//    });
//  }

//  void initMarker(request,requestId){
//    var markerIdVal = requestId;
//    final MarkerId markerId = MarkerId(markerIdVal);
//    final Marker marker = Marker(
//      markerId: markerId,
//      position: LatLng(request['location'].latitude,request['location'].longitude),
//      infoWindow: InfoWindow(title: "Fetched Markers",snippet: request["address"]),
//    );
//    setState(() {
//      markers[markerId] = marker;
//      print(markerId);
//    });
//  }
//
  void _onMapCreated(GoogleMapController controller){
    controller.setMapStyle(Utils.mapStyles);
    _controller.complete(controller);
  }
//
//  void _onMapTypeButtonPressed(){
//    setState(() {
//      _currentMapType = _currentMapType == MapType.normal ? MapType.satellite : MapType.normal;
//    });
//  }
//
  void _onCameraMove(CameraPosition position){
    _lastMapPosition = position.target;
  }
//
//  void _onAddMarkerButtonPressed(){
//    setState(() {
//      _markers.add(Marker(
//        markerId: MarkerId(_lastMapPosition.toString()),
//        position: _lastMapPosition,
//        infoWindow: InfoWindow(
//
//        ),
//      ));
//    });
//  }

//  Widget button(Function function,IconData icon){
//    return FloatingActionButton(
//      onPressed: function,
//      materialTapTargetSize: MaterialTapTargetSize.padded,
//      backgroundColor: Colors.blue,
//      child: Icon(
//        icon,
//        size: 36.0,
//      ),
//    );
//  }

  void handleClick(String value) {
    switch (value) {
      case 'Click':
        {
          Navigator.push(
              context, MaterialPageRoute(builder: (context) => Capture()));
        }
        break;
//        case 'Upload':
//          selectFromImagePicker();
//          break;
    }
  }

  @override
  Widget build(BuildContext context){

//    CameraPosition initialLocation = CameraPosition(
//        zoom: CAMERA_ZOOM,
//        bearing: CAMERA_BEARING,
//        tilt: CAMERA_TILT,
//        target: SOURCE_LOCATION
//    );

    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.transparent,
          actions: <Widget>[
            PopupMenuButton<String>(
              onSelected: handleClick,
              itemBuilder: (BuildContext context) {
                return {'Click'}.map((String choice) {
                  return PopupMenuItem<String>(
                    value: choice,
                    child: Text(choice),
                  );
                }).toList();
              },
            ),
          ],
        ),
//        appBar: AppBar(
//          title: Text('Maps Sample App'),
//          backgroundColor: Colors.lightGreen,
//        ),
        /*onMapCreated: method that is called on map creation and takes a MapController as a parameter.
          initialCameraPosition: required parameter that sets the starting camera position. Camera position describes which part of the world you want the map to point at.
          mapController: manages camera function (position, animation, zoom). This pattern is similar to other controllers available in Flutter, for example TextEditingController.*/
        body: Stack(
        children: <Widget>[
          Container(
          child: marksList.length == 0 ? new Text('No markers found') :
          GoogleMap(
            onMapCreated: _onMapCreated,
            initialCameraPosition: CameraPosition(
              target: LatLng(18.4446,73.7846),
              zoom: 12.0,
            ),
            mapType: _currentMapType,
            markers: Set.from(marksList),
            onCameraMove: _onCameraMove,
          ),
        ),
    ],
      ),
      ),
    );
  }

//  Set<Marker> _createMarker(){
//    return <Marker>[
//      Marker(
//        markerId: MarkerId("home"),
//        position: LatLng(position.latitude,position.longitude),
//        icon: BitmapDescriptor.defaultMarker,
//        infoWindow: InfoWindow(title: "home",snippet: _address),
//      ),
//    ].toSet();
//  }

//  Widget mapWidget(){
//    return Stack(
//      children: <Widget>[
//        GoogleMap(
//          onMapCreated: _onMapCreated,
//          initialCameraPosition: CameraPosition(
//            target: LatLng(position.latitude,position.longitude),
//            zoom: 12.0,
//          ),
//          mapType: _currentMapType,
//          markers: Set<Marker>.of(markers.values),
//          onCameraMove: _onCameraMove,
//        ),
//        Padding(
//          padding: EdgeInsets.all(16.0),
//          child: Align(
//            alignment: Alignment.topRight,
//            child: Column(
//              children: <Widget>[
//                button(_onMapTypeButtonPressed,Icons.map),
//                SizedBox(height: 16.0,),
//                button(_onAddMarkerButtonPressed,Icons.add_location),
//
//              ],
//            ),
//          ),
//        ),
//      ],
//    );
//  }
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