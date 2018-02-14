
--==============================================================================
-- DBA should run these commands
--==============================================================================

--CREATE ROLE COMM_WDK_W NOT IDENTIFIED;
--CREATE ROLE COMM_WDK_W NOT IDENTIFIED;

--==============================================================================
-- Basic grants for read and write roles
--==============================================================================

GRANT EXECUTE ON SYS_SHARED.SHARED_USER TO COMM_WDK_W;

GRANT SELECT ON SYS.DBA_DATA_FILES TO COMM_WDK_W;
GRANT SELECT ON SYS.DBA_TEMP_FILES TO COMM_WDK_W;

GRANT SELECT ON V_$MYSTAT TO COMM_WDK_W;
GRANT SELECT ON V_$SESSION TO COMM_WDK_W;
GRANT SELECT ON V_$TRANSACTION TO COMM_WDK_W;

GRANT CREATE CLUSTER TO COMM_WDK_W;
GRANT CREATE INDEXTYPE TO COMM_WDK_W;
GRANT CREATE OPERATOR TO COMM_WDK_W;
GRANT CREATE PROCEDURE TO COMM_WDK_W;
GRANT CREATE SEQUENCE TO COMM_WDK_W;
GRANT CREATE TABLE TO COMM_WDK_W;
GRANT CREATE TRIGGER TO COMM_WDK_W;
GRANT CREATE TYPE TO COMM_WDK_W;
GRANT CREATE SESSION TO COMM_WDK_W;

--==============================================================================
-- special permissions for apidb schema
--==============================================================================

GRANT EXECUTE ON APIDB.APIDB_UNANALYZED_STATS TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.IS_DATE TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.IS_NUMBER TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.PARSE_AND_ROUND_NUMBER TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.PARSE_DATE TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.PARSE_NUMBER TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.REVERSE_COMPLEMENT TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.REVERSE_COMPLEMENT_CLOB TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.TAB_TO_STRING TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.VARCHARTAB TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.APIDB_UNANALYZED_STATS TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.IS_DATE TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.IS_NUMBER TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.PARSE_AND_ROUND_NUMBER TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.PARSE_DATE TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.PARSE_NUMBER TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.REVERSE_COMPLEMENT TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.REVERSE_COMPLEMENT_CLOB TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.TAB_TO_STRING TO COMM_WDK_W;
GRANT EXECUTE ON APIDB.VARCHARTAB TO COMM_WDK_W;

GRANT SELECT, INSERT, UPDATE, DELETE ON APIDB.DATASOURCE TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON APIDB.GENEFEATURENAME TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON APIDB.GENEFEATUREPRODUCT TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON APIDB.NEXTGENSEQCOVERAGE TO COMM_WDK_W;

GRANT SELECT ON APIDB.MIGRATION_PKSEQ TO COMM_WDK_W;

--==============================================================================
-- special permissions for apidb_r schema
--==============================================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON APIDB_R.TUNINGFAMILY TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON APIDB_R.TUNINGINSTANCE TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON APIDB_R.TUNINGTABLELOG TO COMM_WDK_W;

--==============================================================================
-- special permissions for announce schema
--==============================================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON ANNOUNCE.CATEGORY TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON ANNOUNCE.MESSAGES TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON ANNOUNCE.MESSAGE_PROJECTS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON ANNOUNCE.PROJECTS TO COMM_WDK_W;

GRANT SELECT ON ANNOUNCE.PROJECTS_ID_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON ANNOUNCE.CATEGORY_ID_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON ANNOUNCE.MESSAGES_ID_PKSEQ TO COMM_WDK_W;

--==============================================================================
-- special permissions for gbrowseusers schema
--==============================================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON GBROWSEUSERS.DBINFO TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON GBROWSEUSERS.OPENID_USERS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON GBROWSEUSERS.SESSIONS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON GBROWSEUSERS.SESSION_TBL TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON GBROWSEUSERS.SHARING TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON GBROWSEUSERS.UPLOADS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON GBROWSEUSERS.USERS TO COMM_WDK_W;

GRANT SELECT ON GBROWSEUSERS.GBROWSE_UID_SEQ TO COMM_WDK_W;

--==============================================================================
-- special permissions for uploads schema
--==============================================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON UPLOADS.USERFILE TO COMM_WDK_W;

GRANT SELECT ON UPLOADS.USERFILE_PKSEQ TO COMM_WDK_W;

--==============================================================================
-- special permissions for userlogins5 schema (WDK tables)
--==============================================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.CONFIG TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.USERS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.USER_ROLES TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.PREFERENCES TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.USER_BASKETS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.FAVORITES TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.CATEGORIES TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.DATASETS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.DATASET_VALUES TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.STEPS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.STRATEGIES TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.STEP_ANALYSIS TO COMM_WDK_W;

GRANT SELECT ON USERLOGINS5.USER_BASKETS_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.FAVORITES_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.CATEGORIES_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.DATASETS_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.DATASET_VALUES_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.STEPS_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.STRATEGIES_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.STEP_ANALYSIS_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.MIGRATION_PKSEQ TO COMM_WDK_W;

--==============================================================================
-- special permissions for userlogins5 schema (apicommon tables)
--==============================================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.COMMENT_EXTERNAL_DATABASE TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.COMMENT_TARGET TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.COMMENT_USERS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.COMMENTFILE TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.COMMENTREFERENCE TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.COMMENTS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.COMMENTSEQUENCE TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.COMMENTSTABLEID TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.COMMENTTARGETCATEGORY TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.EXTERNAL_DATABASES TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.LOCATIONS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.REVIEW_STATUS TO COMM_WDK_W;
GRANT SELECT, INSERT, UPDATE, DELETE ON USERLOGINS5.TARGETCATEGORY TO COMM_WDK_W;

GRANT SELECT ON USERLOGINS5.COMMENTFILE_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.COMMENTREFERENCE_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.COMMENTS_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.COMMENTSEQUENCE_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.COMMENTSTABLEID_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.COMMENTTARGETCATEGORY_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.EXTERNAL_DATABASES_PKSEQ TO COMM_WDK_W;
GRANT SELECT ON USERLOGINS5.LOCATIONS_PKSEQ TO COMM_WDK_W;
