import 'package:flutter/material.dart';
import '../services/auth.dart';
import 'maps.dart';

class SignUp extends StatefulWidget {
  @override
  _SignUpScreenState createState() => _SignUpScreenState();
}

class _SignUpScreenState extends State<SignUp> {

  final AuthService _auth = AuthService();
  final _formKey = GlobalKey<FormState>();

  String email = '';
  String password = '';
  String error = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: ListView(
        children: <Widget>[
          BackButtonWidget(),
          SizedBox(
            height: 20,
          ),
          Padding(
            padding: const EdgeInsets.all(20.0),
            child: Form(
              key: _formKey,
              child: Column(
                children: <Widget>[
//                IconButton(icon: Icon(Icons.person), onPressed: null),
                  TextFormField(
                    decoration: InputDecoration(hintText: 'Email'),
                    validator: (val) => val.isEmpty ? 'Enter an email' : null,
                    onChanged: (val){
                      setState(() {
                        email = val;
                      });
                    },
                  ),
                  SizedBox(height: 20.0,),
                  TextFormField(
                    decoration: InputDecoration(hintText: 'Password'),
                    obscureText: true,
                    validator: (val) => val.length < 6 ? 'Enter a password 6+ characters long' : null,
                    onChanged: (val){
                      setState(() {
                        password = val;
                      });
                    },
                  ),
                  SizedBox(height: 30.0,),
                  RaisedButton(
                    onPressed: () async{
                      if(_formKey.currentState.validate()){
                        dynamic result = await _auth.registerWithEmailAndPassword(email.trim(), password);
                        if (result == null){
                          setState(() {
                            error = 'Please supply a valid email';
                          });
                        }
                        else{
                          Navigator.push(
                        context, MaterialPageRoute(builder: (context) => MyApp()));
                        }
                      }
//                    Navigator.push(
//                        context, MaterialPageRoute(builder: (context) => SignIn()));
                    },
                    color: Colors.green,
                    child: Text(
                      'SIGN UP',
                      style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                          fontSize: 20),
                    ),
                  ),
                  SizedBox(height: 20.0,),
                  Text(
                    error,
                    style: TextStyle(
                      color: Colors.red,
                      fontSize: 14.0,
                    ),
                  ),
                ],),
            ),
          ),
//          Padding(
//            padding: const EdgeInsets.all(20.0),
//            child: Row(
//              children: <Widget>[
//                IconButton(icon: Icon(Icons.mail), onPressed: null),
//                Expanded(
//                    child: Container(
//                        margin: EdgeInsets.only(right: 20, left: 10),
//                        child: TextField(
//                          decoration: InputDecoration(hintText: 'Email'),
//                        )))
//              ],
//            ),
//          ),
//          SizedBox(height: 40,),
//          Padding(
//            padding: const EdgeInsets.all(8.0),
//            child: Row(
//              children: <Widget>[
//                Radio(value: null, groupValue: null, onChanged: null),
//                RichText(text: TextSpan(
//                    text: 'I have accepted the',
//                    style: TextStyle(color: Colors.black),
//                    children: [
//                      TextSpan(text: ' Terms & Condition',style: TextStyle(color: Colors.green,fontWeight: FontWeight.bold))
//                    ]
//                ))
//              ],
//            ),
//          ),
//          SizedBox(
//            height: 5,
//          ),
//          Padding(
//            padding: const EdgeInsets.all(20.0),
//            child: ClipRRect(
//              borderRadius: BorderRadius.circular(5),
//              child: Stack(
//                children: <Widget>[
//                  Container(
//                  height: 60,
//                  child:
//                ]
//
//                ),
//              ),
//            ),

        ],
      ),
      );
  }
}

class BackButtonWidget extends StatelessWidget {
  const BackButtonWidget({
    Key key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 200,
      decoration: BoxDecoration(
          image: DecorationImage(
              fit: BoxFit.fitWidth, image: AssetImage('assets/img/wasteman.png'))),
      child: Positioned(
          child: Stack(
            children: <Widget>[
              Positioned(
                  child: Row(
                    children: <Widget>[
                      IconButton(
                          icon: Icon(
                            Icons.arrow_back_ios,
                            color: Colors.black,
                          ),
                          onPressed: () {
                            Navigator.pop(context);
                          }),
                      Text(
                        'Back',
                        style: TextStyle(
                            color: Colors.black, fontWeight: FontWeight.bold),
                      )
                    ],
                  )),
              Positioned(
                child: Padding(
                  padding: const EdgeInsets.only(left: 20.0,top: 180.0),
                  child: Text(
                    'Create New Account',
                    style: TextStyle(
                        color: Colors.black,
                        fontWeight: FontWeight.bold,
                        fontSize: 18),
                  ),
                ),
              )
            ],
          )),
    );
  }
}