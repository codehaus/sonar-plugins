<?php
/**
 * @package Sonar
 */


  function getAllPeople() {
    $db = $this->getDb();
    $peopleTable = $db[self::$PEOPLE_TABLE];
    foreach ($peopleTable as $people) {
      $this->allPeople[$people['id']] = $people;
    }
    $db[self::$PEOPLE_TABLE] = $this->allPeople;
    return $this->allPeople;
  }

    $first = $options->getStartIndex();
    $max = $options->getCount();
    $networkDistance = $options->getNetworkDistance();
    $ids = $this->getIdSet($userId, $groupId, $


  // duplication
  function getAllPeople() {


    $db =                  $this->getDb();



    $peopleTable = $db[self::$PEOPLE_TABLE];
    foreach (           $peopleTable as                            $people) {










      $this->allPeople[$people['id']] =                  $people;
    }

    $db[self::$PEOPLE_TABLE] =                         $this->allPeople;
    return              $this->allPeople;
  }



?>