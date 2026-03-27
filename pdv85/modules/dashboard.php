<?php
session_start();
// Redirect to login if user is not authenticated
if (!isset($_SESSION['user'])) {
    header('Location: login.php');
    exit;
}
// Dashboard content
?>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <link rel='stylesheet' href='assets/css/bootstrap.min.css'>
</head>
<body>
    <h1>Welcome to the Dashboard</h1>
    <script src='assets/js/bootstrap.bundle.min.js'></script>
</body>
</html>