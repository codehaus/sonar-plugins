int main(int argc, char* argv[])
{
  int x = 0;

  /* This if statement is fine */
  if (x < 0)
  {
    /* Because there is no else-if clause, the final else is not mandatory */
  }
  
  /* If if statement is also fine */
  if (x == 0)
  {
    /* Because there is already a final else, this is fine */
  }
  else
  {
    /* This is the final else */
  }
  
  /* This if statement is bad */
  if (x == 0)
  {
    /* Nothing here */
  }
  else if (x > 0)
  {
    /* Nothing here */
  }
  
  /* This if statement is also bad */
  if (x == 0)
  {
    /* Nothing here */
  }
  else if (x > 0)
  {
    /* Nothing here */
  }
  else if (x < 0)
  {
    /* Nothing here */
  }
  
  /* This if statement is fine */
  if (x == 0)
  {
    /* Nothing here */
  }
  else if (x > 0)
  {
    /* Nothing here */
  }
  else if (x < 0)
  {
    /* Nothing here */
  }
  else
  {
    /* This is the final else clause */
  }
}
