<?php
    $con = mysqli_connect("localhost", "id5250406_walknearn", "walknearn", "id5250406_walknearn");
    
    $email = $_POST["email"];
    $password = $_POST["password"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM Users WHERE user_email = ? AND user_password = ?");
    mysqli_stmt_bind_param($statement, "ss", $email,$password);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $userName, $userEmail, $userPassword, $userWalkCoins);

    $response = array();
    $response["success"] = false;

    while(mysqli_stmt_fetch($statement)){
        $response["success"] = true;
		$response["userName"] = $userName;
		$response["userWalkCoins"] = $userWalkCoins;
    }
    echo json_encode($response);
?>