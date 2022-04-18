--
-- PostgreSQL database dump
--

-- Dumped from database version 13.4
-- Dumped by pg_dump version 14.2

-- Started on 2022-04-12 20:18:06

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 205 (class 1259 OID 24879)
-- Name: certificate_tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.certificate_tag (
                                        id bigint NOT NULL,
                                        certificate_id bigint NOT NULL,
                                        tag_id bigint NOT NULL
);


ALTER TABLE public.certificate_tag OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 24877)
-- Name: certificate_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.certificate_tag ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.certificate_tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    );


--
-- TOC entry 201 (class 1259 OID 24859)
-- Name: gift_certificate; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gift_certificate (
                                         id bigint NOT NULL,
                                         name text NOT NULL,
                                         description text NOT NULL,
                                         price double precision NOT NULL,
                                         duration integer NOT NULL,
                                         create_date date NOT NULL,
                                         last_update_date date NOT NULL
);


ALTER TABLE public.gift_certificate OWNER TO postgres;

--
-- TOC entry 200 (class 1259 OID 24857)
-- Name: gift_certificates_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.gift_certificate ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.gift_certificates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    );


--
-- TOC entry 203 (class 1259 OID 24869)
-- Name: tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tag (
                            id bigint NOT NULL,
                            name text NOT NULL
);


ALTER TABLE public.tag OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 24867)
-- Name: tag_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.tag ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    );


--
-- TOC entry 2869 (class 2606 OID 24883)
-- Name: certificate_tag certificate_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.certificate_tag
    ADD CONSTRAINT certificate_tag_pkey PRIMARY KEY (id);


--
-- TOC entry 2865 (class 2606 OID 24866)
-- Name: gift_certificate gift_certificates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gift_certificate
    ADD CONSTRAINT gift_certificates_pkey PRIMARY KEY (id);


--
-- TOC entry 2867 (class 2606 OID 24876)
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);


--
-- TOC entry 2870 (class 2606 OID 24884)
-- Name: certificate_tag certificate_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.certificate_tag
    ADD CONSTRAINT certificate_id FOREIGN KEY (certificate_id) REFERENCES public.gift_certificate(id) NOT VALID;


--
-- TOC entry 2871 (class 2606 OID 24889)
-- Name: certificate_tag tag_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.certificate_tag
    ADD CONSTRAINT tag_id FOREIGN KEY (tag_id) REFERENCES public.tag(id) NOT VALID;


-- Completed on 2022-04-12 20:18:06

--
-- PostgreSQL database dump complete
--

ALTER TABLE public.gift_certificate
    ALTER COLUMN create_date TYPE timestamp without time zone ;

ALTER TABLE public.gift_certificate
    ALTER COLUMN last_update_date TYPE timestamp without time zone ;