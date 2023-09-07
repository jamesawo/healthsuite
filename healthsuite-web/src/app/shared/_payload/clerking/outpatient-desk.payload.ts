import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { SharedPayload } from '@app/shared/_payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { DepartmentPayload } from '@app/modules/settings';
import { ClerkDoctorRequestPayload } from '@app/shared/_payload/clerking/clerk-request.payload';

export enum YesNoEnum {
    YES = 'YES',
    NO = 'NO',
}

export class AbdomenFormPayload {
    id: number;
    movesWithRespiration: YesNoEnum;
    distended: YesNoEnum;
    visiblePeripheralVein: YesNoEnum;
    scarificationMarks: YesNoEnum;
    shape: string;
    hanialOrificesIntact: YesNoEnum;
    palpationLiverEnlargement: YesNoEnum;
    palpationKidneyEnlargement: YesNoEnum;
    palpationSpleenEnlargement: YesNoEnum;
    palpationOtherMasses: YesNoEnum;
    ascultations: YesNoEnum;
    ascitis: YesNoEnum;

    constructor() {
        this.id = undefined;
        this.movesWithRespiration = undefined;
        this.distended = undefined;
        this.visiblePeripheralVein = undefined;
        this.scarificationMarks = undefined;
        this.shape = undefined;
        this.hanialOrificesIntact = undefined;
        this.palpationLiverEnlargement = undefined;
        this.palpationKidneyEnlargement = undefined;
        this.palpationSpleenEnlargement = undefined;
        this.palpationOtherMasses = undefined;
        this.ascultations = undefined;
        this.ascitis = undefined;
    }
}

export class ActualDiagnosisFormPayload {
    id: number;
    otherDiagnosis: string;
    diseases: number[];
    diseasesArray: { id: number; name: string; code: string; checked: boolean }[];

    constructor() {
        this.id = undefined;
        this.otherDiagnosis = undefined;
        this.diseases = [];
        this.diseasesArray = [];
    }
}

export class BackgroundHistoryFormPayload {
    id: number;
    presentingComplaint: string;
    historyOfPresentingComplaint: string;
    reviewOfSystem: string;
    pastMedicalAndSurgicalHistory: string;
    psychiatricHistory: string;
    gynaecologyHistory: string;
    paediatricHistory: string;
    drugHistory: string;
    immunizationHistory: string;
    travelHistory: string;
    familyHistory: string;

    constructor() {
        this.id = undefined;
        this.presentingComplaint = undefined;
        this.historyOfPresentingComplaint = undefined;
        this.reviewOfSystem = undefined;
        this.pastMedicalAndSurgicalHistory = undefined;
        this.psychiatricHistory = undefined;
        this.gynaecologyHistory = undefined;
        this.paediatricHistory = undefined;
        this.drugHistory = undefined;
        this.immunizationHistory = undefined;
        this.travelHistory = undefined;
        this.familyHistory = undefined;
    }
}

export class CardioVascularFormPayload {
    id: number;
    pulseRate: string;
    bloodPressure: string;
    jugularVenousPressure: string;
    apexBeat: string;
    heartSound: string[];

    constructor() {
        this.id = undefined;
        this.pulseRate = undefined;
        this.bloodPressure = undefined;
        this.jugularVenousPressure = undefined;
        this.apexBeat = undefined;
        this.heartSound = [];
    }
}

export class ClinicalAssessmentFormPayload {
    id: string;
    clinicalAssessment: string;
    provisionalDiagnosis: string;
    treatmentPlan: string;
    recordInvestigationResults: string;
    followUpNote: string;
    hasFollowUpNote: boolean;

    constructor() {
        this.id = undefined;
        this.clinicalAssessment = undefined;
        this.provisionalDiagnosis = undefined;
        this.treatmentPlan = undefined;
        this.recordInvestigationResults = undefined;
        this.followUpNote = undefined;
        this.hasFollowUpNote = false;
    }
}

export class InformantDetailsPayload {
    id: number;
    informantName: string;
    informantPhoneNumber: string;
    informantRelationship: SharedPayload;

    constructor() {
        this.id = undefined;
        this.informantName = undefined;
        this.informantPhoneNumber = undefined;
        this.informantRelationship = undefined;
    }
}

export class MusculoSkeletalFormPayload {
    id: number;
    muscleBulk: string;
    perRectumExamination: string;
    tone: string;
    rigidity: YesNoEnum;
    reflexes: string;
    spasticity: string;

    constructor() {
        this.id = undefined;
        this.muscleBulk = undefined;
        this.perRectumExamination = undefined;
        this.tone = undefined;
        this.rigidity = undefined;
        this.reflexes = undefined;
        this.spasticity = undefined;
    }
}

export class NeurologicalExaminationPayload {
    id: number;
    gait: string;
    abnormalMovement: string;

    constructor() {
        this.id = undefined;
        this.gait = undefined;
        this.abnormalMovement = undefined;
    }
}

export class PerieneumFormPayload {
    id: number;
    chaperone: string;
    externalGenitalia: string;
    perRectumExamination: string;
    anyOtherLesions: string;
    vaginalExamination: string;

    constructor() {
        this.id = undefined;
        this.chaperone = undefined;
        this.externalGenitalia = undefined;
        this.perRectumExamination = undefined;
        this.anyOtherLesions = undefined;
        this.vaginalExamination = undefined;
    }
}

export class PhysicalExaminationFormPayload {
    id: string;
    levelOfConsciousness: string;
    patientType: string;
    febril: YesNoEnum;
    pallor: YesNoEnum;
    dehydration: string;
    cyanosis: YesNoEnum;
    jaundice: YesNoEnum;

    constructor() {
        this.id = undefined;
        this.levelOfConsciousness = undefined;
        this.patientType = undefined;
        this.febril = undefined;
        this.pallor = undefined;
        this.dehydration = undefined;
        this.cyanosis = undefined;
        this.jaundice = undefined;
    }
}

export class SystemicExaminationFormPayload {
    id: YesNoEnum;
    dyspnoea: YesNoEnum;
    paroxysmalNocturnalDyspnoea: YesNoEnum;
    positionOfTrachea: string;
    percussionNote: YesNoEnum;
    respiratoryNote: string;
    orthepnoea: YesNoEnum;
    chestMovement: YesNoEnum;
    auscultation: YesNoEnum;

    constructor() {
        this.id = undefined;
        this.dyspnoea = undefined;
        this.paroxysmalNocturnalDyspnoea = undefined;
        this.positionOfTrachea = undefined;
        this.percussionNote = undefined;
        this.respiratoryNote = undefined;
        this.orthepnoea = undefined;
        this.chestMovement = undefined;
        this.auscultation = undefined;
    }
}

export class OtherInformationPayload {
    otherInfo: string;

    constructor() {
        this.otherInfo = undefined;
    }
}

export class OutPatientDeskPayload {
    id: number;
    specialityUnit: SharedPayload;
    patient: PatientPayload;
    otherInformation: OtherInformationPayload;
    backgroundHistory: BackgroundHistoryFormPayload;
    clinicalAssessment: ClinicalAssessmentFormPayload;
    informantDetail: InformantDetailsPayload;
    physicalExamination: PhysicalExaminationFormPayload;
    systemicExamination: SystemicExaminationFormPayload;
    cardioVascularForm: CardioVascularFormPayload;
    abdomenForm: AbdomenFormPayload;
    perieneumForm: PerieneumFormPayload;
    musculoSkeletalForm: MusculoSkeletalFormPayload;
    neurologicalExamination: NeurologicalExaminationPayload;
    actualDiagnosisForm: ActualDiagnosisFormPayload;
    capturedBy: UserPayload;
    captureFromLocation: DepartmentPayload;
    hasInformantDetails: boolean;
    templateName: string;
    isSaveAsTemplate: boolean;
    doctorRequest: ClerkDoctorRequestPayload;

    constructor() {
        this.id = undefined;
        this.backgroundHistory = new BackgroundHistoryFormPayload();
        this.clinicalAssessment = new ClinicalAssessmentFormPayload();
        this.specialityUnit = {};
        this.patient = new PatientPayload();
        this.otherInformation = new OtherInformationPayload();
        this.informantDetail = new InformantDetailsPayload();
        this.physicalExamination = new PhysicalExaminationFormPayload();
        this.systemicExamination = new SystemicExaminationFormPayload();
        this.cardioVascularForm = new CardioVascularFormPayload();
        this.abdomenForm = new AbdomenFormPayload();
        this.perieneumForm = new PerieneumFormPayload();
        this.musculoSkeletalForm = new MusculoSkeletalFormPayload();
        this.neurologicalExamination = new NeurologicalExaminationPayload();
        this.actualDiagnosisForm = new ActualDiagnosisFormPayload();
        this.capturedBy = new UserPayload();
        this.captureFromLocation = new DepartmentPayload();
        this.hasInformantDetails = false;
        this.templateName = undefined;
        this.isSaveAsTemplate = false;
        this.doctorRequest = undefined;
    }
}

export enum ClinicDeskEnum {
    GENERAL_OUTPATIENT_DESK = 'GENERAL_OUTPATIENT_DESK',
    GENERAL_CLERKING_DESK = 'GENERAL_CLERKING_DESK',
}

export interface ClerkingTemplateSearchPayload {
    id: number;
    templateName: string;
    savedDate: string;
    savedTime: string;
    savedBy: UserPayload;
    deskEnum: ClinicDeskEnum;
    deskTitle: string;
    code: string;
}

export class GeneralClerkDeskPayload {
    id: number;
    patient: PatientPayload;
    clerkedBy: UserPayload;
    consultant: UserPayload;
    specialityUnit: SharedPayload;
    hasInformantDetail: boolean;
    informantDetails: InformantDetailsPayload;
    followUpNote: string;
    backgroundHistory: BackgroundHistoryFormPayload;
    actualDiagnosis: ActualDiagnosisFormPayload;
    location: DepartmentPayload;
    isSaveAsTemplate: boolean;
    isUseFollowUpNote: boolean;
    saveAsTemplateName: string;
    provisionalDiagnosis: string;
    doctorRequest: ClerkDoctorRequestPayload;

    constructor() {
        this.id = undefined;
        this.patient = new PatientPayload();
        this.clerkedBy = undefined;
        this.consultant = undefined;
        this.specialityUnit = undefined;
        this.hasInformantDetail = false;
        this.informantDetails = new InformantDetailsPayload();
        this.followUpNote = undefined;
        this.backgroundHistory = new BackgroundHistoryFormPayload();
        this.actualDiagnosis = new ActualDiagnosisFormPayload();
        this.location = undefined;
        this.isSaveAsTemplate = false;
        this.isUseFollowUpNote = false;
        this.saveAsTemplateName = undefined;
        this.provisionalDiagnosis = undefined;

    }

}
