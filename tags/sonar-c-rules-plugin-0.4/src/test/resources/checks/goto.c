

static void shortFunction(Agraph_t * g, Agedge_t * e)
{
    agxget (e, sym);
}

static void longFunction(Agraph_t * g, Agedge_t * e)
{
    goto done;
    if (sym) agsafeset (f, HEADPORT_ID, agxget (e, sym), "");
    done :
    if (sym) agsafeset (f, TAILPORT_ID, agxget (e, sym), "");
}