import 'dart:io';

import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_storage/firebase_storage.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:tflite/tflite.dart';
import 'package:image_picker/image_picker.dart';
import 'package:image/image.dart' as img;

//void main(){
//  runApp(CaptureImg());
//}

//class CaptureImg extends StatelessWidget {
//  @override
//  Widget build(BuildContext context) {
//    return MaterialApp(
//      debugShowCheckedModeBanner: false,
//      home: Capture(),
//    );
//  }
//}

class Capture extends StatefulWidget {
  Capture(this.latitude,this.longitude) : super();

  final double latitude,longitude;
//  final String area,pincode;

  @override
  _CaptureState createState() => _CaptureState();
}

class _CaptureState extends State<Capture> {

  File _image;

  double _imageWidth;
  double _imageHeight;
  bool _busy = false;
  List _recognitions;

  @override
  void initState(){
    super.initState();
    _busy = true;

    loadModel().then((val){
      setState(() {
        _busy = false;
      });
    });
  }

  loadModel() async{
    Tflite.close();
    try{
      String res;
      res = await Tflite.loadModel(
        model:"assets/tflite/detect_garbage.tflite",
        labels:"assets/tflite/label_map.pbtxt",
      );
      print(res);
    }on PlatformException{
      print("Failed to load model");
    }
  }

  selectFromImagePicker() async{
    var image = await ImagePicker.pickImage(source: ImageSource.camera);
    if(image == null)return;
    setState(() {
      _busy = true;
    });
    predictImage(image);
  }

  predictImage(File image) async{
    if(image == null)return;
    await detector(image);

    FileImage(image).resolve(ImageConfiguration()).addListener((ImageStreamListener((ImageInfo info, bool _){
      setState(() {
        _imageWidth = info.image.width.toDouble();
        _imageHeight = info.image.height.toDouble();
      });
    })));

    setState(() {
      _image = image;
      _busy = false;
    });
  }

//  StorageUploadTask _uploadTask;

  String imageRef;

  DatabaseReference database = FirebaseDatabase.instance.reference().child("Cleaned");

  Future<String> upload() async{
//    final FirebaseStorage _storage = FirebaseStorage(storageBucket: 'gs://smart-waste-locator.appspot.com');
    String filepath = '${DateTime.now()}.png';
    final StorageReference firebaseStorageRef = FirebaseStorage.instance.ref().child(filepath);
    StorageUploadTask task = firebaseStorageRef.putFile(_image);
//    imageRef = (await (await task.onComplete).ref.getDownloadURL()).toString();

//    Uri downloadUrl = (await task.future).downloadUrl;
    var dowurl = await (await task.onComplete).ref.getDownloadURL();

    imageRef = dowurl.toString();

//    if(task.isComplete){
//      imageRef = await .getDownloadURL().toString();
////      print(imageRef);
//      Text('🔥🔥🔥');
//    }

    var count = 0;
    database.child("'image - $count'").push().set({
      'image' : imageRef,
      'time' : DateTime.now().toString(),
    });

    count++;
  }

//  enableUpload() async{
//    IconButton(
//      icon: Icon(Icons.add_photo_alternate),
//      tooltip: "Upload image",
//      onPressed: upload,
//    );
//  }

  detector(File image) async{
    var recognitions = await Tflite.detectObjectOnImage(
      path: image.path,
      imageMean: 0.0,
      imageStd: 255.0,
      numResultsPerClass: 1,
      threshold: 0.3,
      asynch: true,
    );

    setState(() {
      _recognitions = recognitions;
    });
  }

  List<Widget> renderBoxes(Size screen){
    if(_recognitions == null) {
      upload();
      return [];
    }
    if(_imageWidth == null || _imageHeight == null) {
      upload();
      return [];
    }

    double factorX = screen.width;
    double factorY = _imageHeight / _imageHeight * screen.width;

    Color c = Colors.lightBlue;

    return _recognitions.map((re){
      return Positioned(
        left : re["rect"]["x"] * factorX,
        top: (re["rect"]["y"] * factorY) ,
        width: re["rect"]["w"] * factorX,
        height: re["rect"]["h"] * factorY,
        child : Container(
          decoration: BoxDecoration(
            border: Border.all(
              color: c,
              width: 3,
            ),
          ),
          child: Text("Waste ${(re["confidenceInClass"] * 100).toStringAsFixed(0)}",
            style: TextStyle(
              background: Paint()..color = c,
              color: Colors.white,
              fontSize: 15,
            ),
          ),
        ),
      );
    }).toList();
  }

  @override
  Widget build(BuildContext context) {

//    if(_uploadTask != null){
//      return StreamBuilder<StorageTaskEvent>(
//        stream: _uploadTask.events,
//        builder: (context,snapshot){
//          var event = snapshot?.data?.snapshot;
//
//          double progressPercent = event != null
//              ? event.bytesTransferred / event.totalByteCount
//              : 0;
//
//          return Column(
//            children: [
//              if(_uploadTask.isComplete)(
//                Text('🔥🔥🔥')
//          ),
//              LinearProgressIndicator(
//                value: progressPercent,
//              ),
//                Text(
//                    '${(progressPercent * 100).toStringAsFixed(2)}',
//                ),
//            ],
//          );
//        }
//      );
//    }

    Size size = MediaQuery.of(context).size;

    List<Widget> stackChildren = [];

    stackChildren.add(Positioned(
      top: 0.0,
      left: 0.0,
      width: size.width,
      height: size.height/2,
      child: _image == null ? Center(child: Text("No image selected", style: TextStyle(fontSize: 25.0,fontWeight: FontWeight.bold),)) : Image.file(_image),
    ));

    stackChildren.addAll(
      renderBoxes(size),
    );

    if(_busy){
      stackChildren.add(Center(
        child: CircularProgressIndicator(),
      ));
    }

    void handleClick(String value) {
      switch (value) {
        case 'Click':
          selectFromImagePicker();
          break;
//        case 'Upload':
//          selectFromImagePicker();
//          break;
      }
    }

    return Scaffold(
      appBar: AppBar(
        title: Text("Maps Sample App"),
          backgroundColor: Colors.lightGreen,
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

//      floatingActionButton: FloatingActionButton(
//        child: Icon(Icons.image),
//        tooltip: "Pick image from gallery",
//        onPressed: selectFromImagePicker,
//      ),



      body: Stack(
            children: <Widget>[
              Stack(
                children: stackChildren,
                ),
              Padding(
                padding: EdgeInsets.only(top:420.0,left: 100.0),
                child: Text(
                  (widget.latitude).toString(),
                  style: TextStyle(
                    fontSize: 21.0,
                  ),
                ),
              ),
              Padding(
                padding: EdgeInsets.only(top:470.0,left: 100.0),
                child: Text(
                  (widget.longitude).toString(),
                  style: TextStyle(
                    fontSize: 21.0,
                  ),
                ),
              ),
          ],
          ),
    );
  }
}
