ALTER TABLE IF EXISTS public.tag
    ADD CONSTRAINT unique_name UNIQUE (name);