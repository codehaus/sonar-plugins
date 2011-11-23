#include <stdio.h>

int main(int argc, char* argv[])
{
  int x = 0;
  
  if (x == 0) printf("x is equal to 0.\n"); /* NOK */
  
  if (x == 0)
  {
    printf("x is equal to 0.\n");
  }
  
  if (x == 1)
  {
    printf("x is equal to 1.\n");
  }
  else printf("x is different from 1.\n"); /* NOK */
  
  if (x == 1)
  {
    printf("x is equal to 1.\n");
  }
  else
  {
    printf("x is different from 1.\n");
  }
  
  if (x == 1)
  {
    printf("x is equal to 1.\n");
  }
  else if (x == 2) printf("x is equal to 2.\n"); /* NOK */
  
  if (x == 1)
  {
    printf("x is equal to 1.\n");
  }
  else if (x == 2)
  {
    printf("x is equal to 2.\n");
  }
  
  if (x == 1) printf("x is equal to 1.\n"); /* NOK */
  else
  {
    printf("x different from 1.\n");
  }
  
  if (x == 1) if (x == 2) { printf("hmm\n"); } /* NOK */
              else { printf("uuh...\n"); }
  else { printf("oh!\n"); }

  return 0;
}
