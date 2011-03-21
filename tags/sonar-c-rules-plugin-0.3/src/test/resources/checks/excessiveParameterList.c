
static void methodWithoutParameter()
{
    agxget (e, sym);
}

static void methodWithoutTooManyParameters(Agraph_t * g, Agedge_t * e)
{
    agxget (e, sym);
}

static void methodWithTooManyParameters(Agraph_t * g, Agedge_t * e, int i)
{
    if (sym) agsafeset (f, HEADPORT_ID, agxget (e, sym), "");
    if (sym) agsafeset (f, TAILPORT_ID, agxget (e, sym), "");
}