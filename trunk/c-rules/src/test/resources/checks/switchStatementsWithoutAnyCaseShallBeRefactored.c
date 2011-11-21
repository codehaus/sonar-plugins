#include <stdio.h>

int main(int argc, char* argv[])
{
  switch (1) printf("This is unreachable code.\n"); /* NOK */
  
  switch (1) /* NOK */
  {
    printf("This is unreachable too.\n");
    printf("But within a compound statement.\n");
  }

  switch (1) /* NOK */
  {
    default:
      printf("I would feel better outside of a switch statement!\n");
  }
  
  switch (1) /* NOK */
  {
    default:
      switch (1)
      {
        printf("This is some dead code, but not detected by this jira ticket!\n");
        case 0:
        case 1:
        case 2:
          printf("Inner switch is fine!");
          break;
      }
      break;
  }
  
  switch (1)
  {
    case 0:
      printf("Case 0\n");
      break;
    case 1:
      printf("Case 1\n");
      break;
  }

  return 0;
}
