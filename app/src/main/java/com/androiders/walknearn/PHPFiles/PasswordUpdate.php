<?php

	$con = mysqli_connect("localhost", "id5250406_walknearn", "walknearn", "id5250406_walknearn");
    
    $email = $_POST["email"];
    $oldpassword = $_POST["oldpassword"];
    $newpassword = $_POST["newpassword"];
    
    $response = array();
    
    $CheckQuery = "SELECT * FROM Users WHERE user_email = '$email' AND user_password = '$oldpassword' ";
    $ExecuteQuery = mysqli_query($con,$CheckQuery);
    if(mysqli_num_rows($ExecuteQuery) > 0)
    {
        $response["exists"] = true;
        $UpdateQuery = "UPDATE Users SET user_password ='$newpassword' WHERE user_email = '$email' ";
        $ExecUpdateQuery = mysqli_query($con,$UpdateQuery);
	    if($ExecUpdateQuery)
            $response["success"] = true;
        else
            $response["success"] = false;
    }
    else
	    $response["exists"] = false;
    
    echo json_encode($response);
?>