#include <stdio.h>
#include <stdbool.h>

int myfunction(void)
{
  return 0;
}

int main(int argc, char* argv[])
{  
  /* isInfinite(), getElement() */
  
  int x = 0, y = 0, z = 0;
  int *ptr = &x;
  
  for (; ;) break;
  for (x = 0; ;) break;
  for (; y < 0;) break;
  for (; ; z++) break;
  for (x = 0; y < 0;) break;
  for (x = 0; ; z++) break;
  for (; y < 0; z++) break;
  for (x = 0; y < 0; z++) break;
  
  for (int x = 0; ;) break;
  for (int x = 0; y < 0;) break;
  for (int x = 0; ; z++) break;
  for (int x = 0; y < 0; z++) break;
  
  /* getFirstElementAssignedVariable() */
  
  for (; ;) break;
  for (x = 0; ;) break;
  for (int a = 0; ;) break;
  for (x = myfunction(); ;) break;
  for (int z = myfunction(); ;) break;
  for (x = 0, y = 0; ;) break;
  for (int a = 0, b = 0; ;) break;
  for (int *ptr = &x; ;) break;
  for (int a[1] = {0}; ;) break;
  for (myfunction(); ;) break;
  for (x; ;) break;
  for (int z; ;) break;
  for (x += 5; ;) break;
  for (*ptr = 1; ;) break;
  
  /* getThirdElementIncrementedVariable() */
  double d = 0.0;
  
  for (; ;) break;
  for (; ; x++) break;
  for (; ; ++y) break;
  for (; ; z--) break;
  for (; ; --x) break;
  for (; ; y++) break;
  for (; ; z += 1) break;
  for (; ; x -= 1) break;
  for (; ; x *= 1) break;
  for (; ; x /= 1) break;
  for (; ; x %= 1) break;
  for (; ; x = x + 1) break; // limitation
  for (; ; d += 0.1) break;
  for (; ; d += 0.1f) break;
  for (; ; d -= 0.1f) break;
  for (; ; x) break;
  for (; ; myfunction()) break;
  for (; ; *ptr++) break;
  
  /* getLoopCounterVariable() */
  union my_union {
    int i;
  } my_union_var;
  
  union my_union *my_union_ptr = &my_union_var;
  int a[1];
  
  for (x = 0; x < 10; x++) break;
  for (; x < 10; x++) break;
  for (x = 0; x >= 10; x++) break;
  for (x = 0; (x >= 10 && !y) || z; x++) break;
  for (y = 0; x < 10; x++) break;
  for (x = 0; x == 10; x++) break;
  for (x = 0; x = 10; x++) break;
  for (x = 0; x < 10 && *ptr; x++) break;
  for (x = 0; &x < 10; x++) break;
  for (my_union_var.i = 0; my_union_var.i < 10; my_union_var.i++) break;
  for (my_union_ptr->i = 0; my_union_ptr->i < 10; my_union_ptr->i++) break;
  for (a[0] = 0; a[0] < 10; a[0]++) break;
  for (x = 0; x < 10 && myfunction(); x++) break;
  for (x = 0; x < 10; x+= myfunction()) break;
  
  return 0;
}
