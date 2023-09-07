export class AntibioticsPayload {
    id: number;
    name: string;
    organism: {
        id: number;
        name: string;
    };

    constructor() {
        this.id = null;
        this.name = '';
        this.organism = {
            id: null,
            name: '',
        };
    }
}
