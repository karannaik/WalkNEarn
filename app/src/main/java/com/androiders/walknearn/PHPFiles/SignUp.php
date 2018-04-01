<?php

	$con = mysqli_connect("localhost", "id5250406_walknearn", "walknearn", "id5250406_walknearn");
    
	$email = $_POST["user_email"];
	$password = $_POST["user_password"];
    
    $statement = mysqli_prepare($con, "INSERT INTO Users(user_email,user_password) VALUES(?,?)");
    mysqli_stmt_bind_param($statement, "ss", $email,$password);
    mysqli_stmt_execute($statement);
    
    $response = array();
    $response["success"] = true;  
    
    echo json_encode($response);
?>