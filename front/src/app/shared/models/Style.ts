import {EnumerableEntity} from "./EnumerableEntity";

export interface Style extends EnumerableEntity {
    id: number;
    name: string;
    description: string;
    parentId: number;
    parentName: string;
}