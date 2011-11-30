#include <stdio.h>

int main(int argc, char* argv[])
{  
  int x, y;

  for (x = 0; x < 10; x++)
  {
  
  }
  
  for (x = 0; x < 10; x++)
  {
    x = 11; /* NOK */
  }
  
  for (x = 0; x < 10; x++)
  {
    x += 11; /* NOK */
  }
  
  for (x = 0; x < 10; x++)
  {
    x *= 11; /* NOK */
  }
  
  for (x = 0; x < 10; x++)
  {
    x /= 1; /* NOK */
  }
  
  for (x = 0; x < 10; x++)
  {
    x %= 1; /* NOK */
    break;
  }
  
  for (x = 0; x < 10; x++)
  {
    x++; /* NOK */
  }
  
  for (x = 0; x < 10; x++)
  {
    x--; /* NOK */
    break;
  }
  
  for (x = 0; x < 10; x++)
  {
    ++x; /* NOK */
  }
  
  for (x = 0; x < 10; x++)
  {
    --x; /* NOK */
    break;
  }
  
  for (x = 0; x < 10; x++)
  {
    for (y = 0; y < 5; y++)
    {
      y = 10; /* NOK */
    }
  }
  
  for (x = 0; x < 10; x++)
  {
    for (y = 0; y < 5; y++)
    {
      x = 10; /* NOK */
    }
  }
  
  for (x = 0; x < 10; x++)
  {
    for (y = 0; y < 5; y++)
    {
    
    }
    
    y = 10;
  }
  
  for (x = 0; x < 10; x++)
  {
    for (y = 0; y < 5; x = 0) /* NOK */
    {
      break;
    }
    
    break;
  }
  
  return 0;
}
