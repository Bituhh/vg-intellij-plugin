CREATE OR REPLACE FUNCTION "public"."{{FUNCTION_NAME}}"(i_usr CHARACTER VARYING{{ARGUMENTS}}) RETURNS {{RETURN_TYPE}}
    {{VOLATILITY}}
AS
$body$
DECLARE
    v_user_code  CHAR(4) := '';
    v_my_user_id INTEGER := 0;
BEGIN
    SELECT security_user_id, security_user_code
    INTO v_my_user_id,v_user_code
    FROM {{PREFIX}}_is_user_authorized(i_usr, {{ROLES}});

    -- Code Here
END;
$body$ LANGUAGE plpgsql;
