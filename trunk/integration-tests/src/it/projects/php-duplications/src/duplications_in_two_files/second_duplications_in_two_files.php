<?php
/**
 * Another Class.
 *
 * @package    Sonar
 */
class AnotherClass{

  private function filterResults($peopleById, $options) {
    if (! $options->getFilterBy()) {
      return $peopleById; // no filtering specified
    }
    $filterBy = $options->getFilterBy();
    $op = $options->getFilterOperation();
    if (! $op) {
      $op = CollectionOptions::FILTER_OP_EQUALS; // use this container-specific default
    }
    $value = $options->getFilterValue();
    $filteredResults = array();
    $numFilteredResults = 0;
    foreach ($peopleById as $id => $person) {
      if ($this->passesFilter($person, $filterBy, $op, $value)) {
        $filteredResults[$id] = $person;
        $numFilteredResults ++;
      }
    }
    return $filteredResults;
  }


  public function getData() {
    try {
      $fileName = sys_get_temp_dir() . '/' . $this->jsonDbFileName;
      if (file_exists($fileName)) {
        if (! is_readable($fileName)) {
          throw new Exception("error", ResponseError::$INTERNAL_ERROR);
        }

        $cachedDb = file_get_contents($fileName);
        $jsonDecoded = json_decode($cachedDb, true);
        if ($jsonDecoded == $cachedDb) {
          throw new Exception("error2", ResponseError::$INTERNAL_ERROR);
        }



        return $jsonDecoded;
      } else {
        $jsonDb = Config::get('jsondb_path');
        if (! file_exists($jsonDb) || ! is_readable($jsonDb)) {
          throw new Exception("error", ResponseError::$INTERNAL_ERROR);
        }
        $dbConfig = @file_get_contents($jsonDb);
        $contents = preg_replace('/(?<!http:|https:)\/\/.*$/m', '', preg_replace('@/\\*(?:.|[\\n\\r])*?\\*/@', '', $dbConfig));
        $jsonDecoded = json_decode($contents, true);
        if ($jsonDecoded == $contents) {
          throw new Exception("error", ResponseError::$INTERNAL_ERROR);
        }
        $this->saveDb($jsonDecoded);
        return $jsonDecoded;
      }
    } catch (Exception $e) {
      throw new Exception("error: " . $e->getMessage(), ResponseError::$INTERNAL_ERROR);
    }
  }



  private function passesFilter($person, $filterBy, $op, $value) {
    $fieldValue = $person[$filterBy];
    if (! $fieldValue || (is_array($fieldValue) && ! count($fieldValue))) {
      return false; // person is missing the field being filtered for
    }
    if ($op == CollectionOptions::FILTER_OP_PRESENT) {
      return true; // person has a non-empty value for the requested field
    }
    if (!$value) {
      return false; // can't do an equals/startswith/contains filter on an empty filter value
    }
    // grab string value for comparison
    if (is_array($fieldValue)) {
      // plural fields match if any instance of that field matches
      foreach ($fieldValue as $field) {
        if ($this->passesStringFilter($field, $op, $value)) {
          return true;
        }
      }
    } else {
      return $this->passesStringFilter($fieldValue, $op, $value);
    }

    return false;
  }


}