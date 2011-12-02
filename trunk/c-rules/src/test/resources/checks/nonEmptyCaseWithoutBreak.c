#include <stdio.h>

int main(int argc, char* argv[])
{
  switch (1)
  {
    default:
      printf("Hello 1!\n");
      printf("Hello 2!\n");
      printf("Hello 3!\n");
      break;
  }
  
  switch (0)
  {
    default:
      break;
  }

  switch (1) printf("unreachable code!\n");

  switch (0)
  {
    case 0: /* NOK */
     printf("non-empty case, not terminated by a break.\n");
    case 1:
    case 2:
    default:
      break;    
  }
  
  switch (0)
  {
    case 0: /* NOK */
    case 1:
    case 2:
    default:
      printf("non-empty case, not terminated by a break.\n");
  }
  
  switch (0)
  {
    case 0: /* NOK */
    case 1:
      printf("non-empty case, not terminated by a break.\n");
    case 2:
    default:
      break;
  }
  
  switch (0)
  {
    case 0: /* NOK */
     printf("non-empty case, not terminated by an unconditional break.\n");
     if (0)
     {
       break;
     }
     else
     {
       break;
     }
    case 1:
    case 2:
    default:
      break;    
  }
  
  switch (0)
  {
    case 0:
     printf("non-empty case, terminated by an unconditional break.\n");
     break;
    case 1:
    case 2:
    default:
      break;    
  }

  switch (0)
  {
    case 0:
     printf("non-empty case, terminated by an unconditional break.\n");
     break;
    case 1:
    case 2:
    default:
      printf("Hello 1!\n");
      printf("Hello 2!\n");
      break;    
  }
  
  switch (0)
  {
    default: /* NOK */
      printf("default\n");
  }

  int x;

  switch (0)
  {
    case 3:
      if (0)
      {

      }
      break;
    default:
      break;
  }
  
  switch (0)
    case 0:
    case 1:
      break;
  
  switch (0)
    case 0: /* NOK */
    case 1:
      printf("hello\n");

  return 0;
}
