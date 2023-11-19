package com.example.examen;

public class RestApiMethods {

    //http://localhost/PM01/CreatePerson.php

    public static final String separador = "/";
    public static final String ipadress = "192.168.56.1";
    public static final String RestApi = "EXAMEN";
    public static final String PostRouting = "CreateContact.php";
    public static final String GetRouting = "ListContact.php";

    // Endpoint
    public static final String  EndpointPost = "http://"  + ipadress + separador + RestApi + separador + PostRouting;
    public static final String  EndpointGet = "http://"  + ipadress + separador + RestApi + separador + GetRouting;

}
