<?php
ob_start('ob_gzhandler');

header('Content-Type: application/json');

$query = $_GET['query'];

$authHeader = getallheaders();
$ch = curl_init("http://stoopid.fetedelabiere.ch:8080/api/".$query);

$options = [
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_FOLLOWLOCATION => true,
//   CURLOPT_HTTPAUTH => CURLAUTH_BASIC,
//   CURLOPT_USERPWD => "brewmaster:VerreRideauBasketMirroir666"
];


// Récupérer les en-têtes HTTP
$headers = getallheaders();

// Vérifier si les informations d'authentification sont présentes dans les en-têtes
if (isset($headers['Authorization'])) {
  // Récupérer les informations d'authentification de base à partir de l'en-tête "Authorization"
  $auth = base64_decode(substr($headers['Authorization'], 6));
  list($username, $password) = explode(':', $auth);

  // Ajouter les informations d'authentification à la liste d'options de cURL
  $options[CURLOPT_HTTPAUTH] = CURLAUTH_BASIC;
  $options[CURLOPT_USERPWD] = "$username:$password";
}

curl_setopt_array($ch, $options);

echo curl_exec($ch);

curl_close($ch);
