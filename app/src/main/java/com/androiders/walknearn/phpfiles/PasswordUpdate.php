<?php

	$con = mysqli_connect("localhost", "id5250406_walknearn", "walknearn", "id5250406_walknearn");
    
    $email = $_POST["email"];
    $password = $_POST["password"];
    
    $response = array();
    
    $UpdateQuery = "UPDATE Users SET user_password ='$password' WHERE user_email = '$email' ";
    $ExecUpdateQuery = mysqli_query($con,$UpdateQuery);
	if($ExecUpdateQuery)
        $response["success"] = true;
    else
        $response["success"] = false;
    
    $statement = mysqli_prepare($con, "SELECT * FROM Users WHERE user_email = ? AND user_password = ?");
    mysqli_stmt_bind_param($statement, "ss", $email,$password);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $userName, $userEmail, $userStepCount, $userPassword);
    
    while(mysqli_stmt_fetch($statement)){
		$response["userName"] = $userName;
		$response["userStepCount"] = $userStepCount;
    }
    
    echo json_encode($response);
?>