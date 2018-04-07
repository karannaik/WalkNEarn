<?php

	$con = mysqli_connect("localhost", "id5250406_walknearn", "walknearn", "id5250406_walknearn");
    
    $email = $_POST["email"];
    $name = $_POST["name"];
    
    $response = array();
    $UpdateQuery = "UPDATE Users SET user_name ='$name' WHERE user_email = '$email' ";
    $ExecUpdateQuery = mysqli_query($con,$UpdateQuery);
	if($ExecUpdateQuery)
        $response["success"] = true;
    else
        $response["success"] = false;
    
    echo json_encode($response);
?>