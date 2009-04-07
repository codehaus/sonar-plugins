<?php
/**
 * A Class.
 *
 * @package    Sonar
 */
class AClass{

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

 public function getActivity($userId, $groupId, $appdId, $fields, $activityId, SecurityToken $token) {
    $activities = $this->getActivities($userId, $groupId, $appdId, null, null, null, null, $fields, array($activityId), $token);
    if ($activities instanceof RestfulCollection) {
      $activities = $activities->getEntry();
      foreach ($activities as $activity) {
        if ($activity->getId() == $activityId) {
          return $activity;
        }
      }
    }
    throw new Exception("Error", ResponseError::$NOT_FOUND);
  }

  private function getDatas() {
    $db = $this->getDb();
    $dataTable = $db[self::$DATA_TABLE];
    foreach ($dataTable as $key => $value) {
      $this->allData[$key] = $value;
    }
    $db[self::$DATA_TABLE] = $this->allData;
    return $this->allData;
  }


}