#include <stdio.h>

int main(int argc, char* argv[])
{
  switch (0) printf("0\n");
  
  switch (0) case 0: printf("0\n");
  
  switch (0)
  {
    case 0:
      printf("0\n");
  }

  return 0;
}
