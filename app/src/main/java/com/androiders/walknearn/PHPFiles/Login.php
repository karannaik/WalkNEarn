<?php
    $con = mysqli_connect("localhost", "id5250406_walknearn", "walknearn", "id5250406_walknearn");
    
    $email = $_POST["user_email"];
    $password = $_POST["user_password"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM user WHERE user_email = ? AND user_password = ?");
    mysqli_stmt_bind_param($statement, "ss", $email,$password);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $userName, $userEmail, $userStepCount, $userPassword);
    
    $response = array();
    $response["success"] = false;  
    
    while(mysqli_stmt_fetch($statement)){
        $response["success"] = true;
		$response["userName"] = $userName;
		$response["userEmail"] = $userEmail;
		$response["userStepCount"] = $userStepCount;
		$response["userPassword"] = $userPassword;
    }
    echo json_encode($response);
?>