/**
 * Contains all possible phases and subphases
 *
 * @author Omar Hashmi
 * @version 11.07.2020
 */

public enum Phase {
    NUM_HUMANS,
    NUM_AI,
    PLAYER_NAME,
    CLAIM_COUNTRY,
    INITIAL_DEPLOY_DST,
    INITIAL_DEPLOY_NUM_TROOPS,
    INITIAL_DEPLOY_CONFIRM,
    /** Game over **/
    GAME_OVER,
    /** Deploy phase selecting a source country **/
    DEPLOY_DST,
    /** Deploy phase selecting number of troops to deploy **/
    DEPLOY_NUM_TROOPS,
    /**Deploy phase confirmation**/
    DEPLOY_CONFIRM,
    /** Attack phase selecting a source country **/
    ATTACK_SRC,
    /** Attack phase selecting a destination country **/
    ATTACK_DST,
    /** Attack phase selecting number of troops to send **/
    ATTACK_NUM_TROOPS,
    /** Attack phase confirmation **/
    ATTACK_CONFIRM,
    /** Attack phase selecting number of troops to defend with **/
    DEFEND_NUM_TROOPS,
    /** Attack phase defender confirm **/
    DEFEND_CONFIRM,
    /** Attack phase results, retreat phase selecting troops to send back **/
    RETREAT_NUM_TROOPS,
    /** Retreat phase confirmation **/
    RETREAT_CONFIRM,
    /** Fortify phase selecting a source country **/
    FORTIFY_SRC,
    /** Fortify phase selecting a destination country **/
    FORTIFY_DST,
    /** Fortify phase selecting number of troops to send **/
    FORTIFY_NUM_TROOPS,
    /** Fortify phase confirmation **/
    FORTIFY_CONFIRM,
}
