<?php

	$con = mysqli_connect("localhost", "id5250406_walknearn", "walknearn", "id5250406_walknearn");
    
    $email = $_POST["email"];
    $password = $_POST["password"];
    
    $response = array();
    
    $CheckQuery = "SELECT * FROM Users WHERE user_email = '$email' AND user_password = '$password' ";
    $ExecuteQuery = mysqli_query($con,$CheckQuery);
    if(mysqli_num_rows($ExecuteQuery) > 0) 
        $response["exists"] = true;
	else
	    $response["exists"] = false;
    
    echo json_encode($response);
?>