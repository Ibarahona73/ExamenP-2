package com.example.examen;

public class RestApiMethods {

    //http://localhost/PM01/CreatePerson.php

    public static final String separador = "/";
    public static final String ipadress = "grupo3pm1.atwebpages.com"; //cambiar por su ip

    //public static final String ipadress = "108.181.157.246";
    public static final String RestApi = "api";
    public static final String PostRouting = "create.php";
    public static final String GetRouting = "get.php";
    public static final String PutRouting = "update.php";
    public static final String DeleteRouting = "delete.php";

    //Endpoint

    public static final String  EndpointPost = "http://"  + ipadress + separador + RestApi + separador + PostRouting;
    public static final String  EndpointGet = "http://"  + ipadress + separador + RestApi + separador + GetRouting;
    public static final String  EndpointPut = "http://"  + ipadress + separador + RestApi + separador + PutRouting;
    public static final String  EndpointDel = "http://"  + ipadress + separador + RestApi + separador + DeleteRouting;

}
