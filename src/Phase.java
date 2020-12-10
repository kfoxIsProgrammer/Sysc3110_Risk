/**
 * Contains all possible phases and sub-phases
 *
 * @author Omar Hashmi
 * @version 11.07.2020
 */

public enum Phase {
    /** Game over **/
    GAME_OVER,
    /** Enter the number of human players*/
    NUM_HUMANS,
    /** Enter the number of AI players*/
    NUM_AI,
    /** Enter the names of players*/
    PLAYER_NAME,
    /** Claiming countries at the start*/
    CLAIM_COUNTRY,
    /** Deploy initial units Destination*/
    INITIAL_DEPLOY_DST,
    /** Deploy initial units selecting*/
    INITIAL_DEPLOY_NUM_TROOPS,
    /** Deploying initial units confirmation*/
    INITIAL_DEPLOY_CONFIRM,
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
