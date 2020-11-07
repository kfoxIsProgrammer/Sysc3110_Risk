/**
 * Contains all possible phases and subphases
 *
 * @author Omar Hashmi
 * @version 11.07.2020
 */

public enum Phase {
    /** Deploy phase selecting a source country **/
    DEPLOY_DST,
    /** Deploy phase selecting number of troops to deploy **/
    DEPLOY_ARMY,
    /**Deploy phase confirmation**/
    DEPLOY_CONFIRM,
    /** Attack phase selecting a source country **/
    ATTACK_SRC,
    /** Attack phase selecting a destination country **/
    ATTACK_DST,
    /** Attack phase selecting number of troops to send **/
    ATTACK_ARMY,
    /** Attack phase confirmation **/
    ATTACK_CONFIRM,
    /** Attack phase results, retreat phase selecting troops to send back **/
    RETREAT_ARMY,
    /** Retreat phase confirmation **/
    RETREAT_CONFIRM,
    /** Fortify phase selecting a source country **/
    FORTIFY_SRC,
    /** Fortify phase selecting a destination country **/
    FORTIFY_DST,
    /** Fortify phase selecting number of troops to send **/
    FORTIFY_ARMY,
    /** Fortify phase confirmation **/
    FORTIFY_CONFIRM
}
