<?php
ob_start('ob_gzhandler');

header('Content-Type: application/json');
console.log('test');
console.log($_GET);
$query = $_GET['query'];

$ch = curl_init("http://stoopid.fetedelabiere.ch:8080/api/".$query);

$options = [
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_FOLLOWLOCATION => true,
];

curl_setopt_array($ch, $options);

echo curl_exec($ch);

curl_close($ch);
