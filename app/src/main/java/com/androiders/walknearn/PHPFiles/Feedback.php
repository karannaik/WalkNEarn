<?php
    
    $con = mysqli_connect("localhost", "id5250406_walknearn", "walknearn", "id5250406_walknearn");
    
    $email_to = "jyothsna.kilaru04@gmail.com";
    $email_sub = "WalkNEarn Feedback";
    $email = $_POST["email"];
    $email_msg = $_POST["msg"];
    
    $response = array();
    $headers  = 'MIME-Version: 1.0' . "\r\n";
    $headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";
    $headers .= "From: " + $email + " \r\n";

    $res = mail($email_to,$email_sub,$email_msg,$headers);
    if($res)
        $response["success"] = true;
    else
        $response["success"] = false;
    
    echo json_encode($response);
?>