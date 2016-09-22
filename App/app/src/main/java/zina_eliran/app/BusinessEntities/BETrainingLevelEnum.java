package zina_eliran.app.BusinessEntities;

/**
 * Created by eli on 18/09/2016.
 */
public enum BETrainingLevelEnum {
    SweetPotato  {
        @Override
        public String toString() {
            return "Sweet Potato";
        }
    },
    Lazy,
    Hobby,
    Pro,
    mazeRunner {
        @Override
        public String toString() {
            return "Maze Runner";
        }
    }
}
