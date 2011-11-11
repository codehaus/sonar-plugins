#include <stdio.h>
#include <stdarg.h>

int main(int argc, char* argv[]);
int maxof(int n_args, ...);

int maxof(int n_args, ...)
{
  int max = 0;
  int i;
  int val;
  va_list ap;
  
  va_start(ap, n_args);
  for (i = 0; i < n_args; i++)
  {
    val = va_arg(ap, int);
    if (val > max)
    {
      max = val;
    }
  }
  va_end(ap);
  
  return max;
}

int main(int argc, char* argv[])
{
  printf("The max is: %d\n", maxof(4 /* number of arguments */, 10, -4, 11, 5));
}
