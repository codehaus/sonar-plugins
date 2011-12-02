#include <stdio.h>

int main(int argc, char* argv[])
{  
  int x, y;

  for (x = 0; x < 10; x++)
  {
  
  }
  
  for (x = 0; x < 10; x++)
  {
    x = 11; /* NOK, x at 12 */
  }
  
  for (x = 0; x < 10; x++)
  {
    x += 11; /* NOK, x at 17 */
  }
  
  for (x = 0; x < 10; x++)
  {
    x *= 11; /* NOK, x at 22 */
  }
  
  for (x = 0; x < 10; x++)
  {
    x /= 1; /* NOK, x at 27 */
  }
  
  for (x = 0; x < 10; x++)
  {
    x %= 1; /* NOK, x at 32 */
    break;
  }
  
  for (x = 0; x < 10; x++)
  {
    x++; /* NOK, x at 38 */
  }
  
  for (x = 0; x < 10; x++)
  {
    x--; /* NOK, x at 43 */
    break;
  }
  
  for (x = 0; x < 10; x++)
  {
    ++x; /* NOK, x at 49 */
  }
  
  for (x = 0; x < 10; x++)
  {
    --x; /* NOK, x at 54 */
    break;
  }
  
  for (x = 0; x < 10; x++)
  {
    for (y = 0; y < 5; y++)
    {
      y = 10; /* NOK, y at 62 */
    }
  }
  
  for (x = 0; x < 10; x++)
  {
    for (y = 0; y < 5; y++)
    {
      x = 10; /* NOK, x at 68 */
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
    for (y = 0; y < 5; x = 0) /* NOK, x at 86 */
    {
      break;
    }
    
    break;
  }
  
  return 0;
}
