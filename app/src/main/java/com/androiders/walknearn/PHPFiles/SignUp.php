<?php

	$con = mysqli_connect("localhost", "id5250406_walknearn", "walknearn", "id5250406_walknearn");
    
    $email = $_POST["email"];
    $name = $_POST["name"];
    $password = $_POST["password"];
    
    $response = array();
    
    $CheckQuery = "SELECT * FROM Users WHERE user_email = '$email'";
    $ExecuteQuery = mysqli_query($con,$CheckQuery);
    if(mysqli_num_rows($ExecuteQuery) > 0) 
        $response["exists"] = true;
	else
	{
	    $response["exists"] = false;
	    $InsertQuery = "INSERT INTO Users (user_email,user_password,user_name) VALUES ('$email', '$password','$name')";
	    $ExecInsertQuery = mysqli_query($con,$InsertQuery);
	    if($ExecInsertQuery)
            $response["success"] = true;
        else
            $response["success"] = false;
	}
    
    echo json_encode($response);
?>